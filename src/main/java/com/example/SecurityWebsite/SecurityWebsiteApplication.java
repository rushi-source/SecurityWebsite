package com.example.SecurityWebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableAsync
@SpringBootApplication
public class SecurityWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityWebsiteApplication.class, args);
	}

}
