package com.amalitech.blogging_platform.security; // Adjust to your package

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;
import java.util.Base64;

public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
  private static final int COOKIE_EXPIRY_SECONDS = 180;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            .map(cookie -> {
              byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
              return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(bytes);
            })
            .orElse(null);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
    if (authorizationRequest == null) {
      deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
      return;
    }
    byte[] serialized = SerializationUtils.serialize(authorizationRequest);
    String value = Base64.getUrlEncoder().withoutPadding().encodeToString(serialized);
    addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, value);

  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
    OAuth2AuthorizationRequest requestObj = loadAuthorizationRequest(request);
    if (requestObj != null) {
      deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
    return requestObj;
  }

  private static void addCookie(HttpServletResponse response, String name, String value) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(false); // TODO: use .env to handle this (https only or not)
    cookie.setMaxAge(HttpCookieOAuth2AuthorizationRequestRepository.COOKIE_EXPIRY_SECONDS);
    response.addCookie(cookie);
  }

  private static java.util.Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return java.util.Optional.of(cookie);
        }
      }
    }
    return java.util.Optional.empty();
  }

  private static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
    getCookie(request, name).ifPresent(cookie -> {
      cookie.setValue("");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    });
  }
}