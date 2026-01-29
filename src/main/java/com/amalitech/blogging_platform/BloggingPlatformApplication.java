package com.amalitech.blogging_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BloggingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloggingPlatformApplication.class, args);
	}

}
