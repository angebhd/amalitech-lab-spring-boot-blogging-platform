package com.amalitech.blogging_platform.security; // Adjust to your package

import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.exceptions.UnauthorizedException;
import com.amalitech.blogging_platform.service.AuthService; // Assuming this has your JWT generation logic
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final AuthService authService;
  private final ObjectMapper objectMapper;


  @Autowired
  public OAuth2AuthenticationSuccessHandler(AuthService authService) {
    this.authService = authService;
    this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    try{
      OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
      if (oAuth2User == null)
        throw new UnauthorizedException("Google authentication has failed !");

      AuthDTO.LoginResponse loginResponse = this.authService.processOAuthPostLogin(oAuth2User);
      GenericResponse<AuthDTO.LoginResponse> genericResponse = new GenericResponse<>(HttpStatus.OK, loginResponse);

      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_OK);
      objectMapper.writeValue(response.getOutputStream(), genericResponse);

    }catch(UnauthorizedException e){
      throw e;
    }catch(Exception e){
      throw new UnauthorizedException(e.getMessage());
    }


  }
}