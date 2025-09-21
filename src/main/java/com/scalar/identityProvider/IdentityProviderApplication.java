package com.scalar.identityProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class IdentityProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityProviderApplication.class, args);
	}

	/**
	 * Load environment variables from .env file.
	 */
	static {
		if (System.getProperty("DOTENV_LOADED") == null) {
			Dotenv dotenv = Dotenv.configure().load(); // Load .env file
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue())); // Set as system properties
			System.setProperty("DOTENV_LOADED", "true");

			System.out.println("✅ Environment variables loaded from .env file");
		}
	}

}
