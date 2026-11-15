package com.guilherme.lerabrecaminhos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// O exclude de SecurityAutoConfiguration foi removido: a classe mudou de módulo no Spring Boot 4 e o Spring Security
// não está no classpath (nem antes nem depois), então a exclusão não tinha efeito.
@SpringBootApplication
public class LerAbreCaminhosApplication {

	public static void main(String[] args) {
		SpringApplication.run(LerAbreCaminhosApplication.class, args);
	}

}
