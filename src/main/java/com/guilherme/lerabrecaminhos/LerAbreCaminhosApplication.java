package com.guilherme.lerabrecaminhos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication ( exclude = {SecurityAutoConfiguration.class} )
public class LerAbreCaminhosApplication {

	public static void main(String[] args) {
		SpringApplication.run(LerAbreCaminhosApplication.class, args);
	}

}
