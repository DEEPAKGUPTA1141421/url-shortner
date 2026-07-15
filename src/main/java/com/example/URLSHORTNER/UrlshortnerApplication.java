package com.example.URLSHORTNER;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class UrlshortnerApplication {

	public static void main(String[] args) {
		// Dotenv dotenv = Dotenv.configure()
		// .directory("./src/main/resources") // path to your .env file
		// .load();

		// // Set all env vars so Spring can use them
		// dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(),
		// entry.getValue()));
		SpringApplication.run(UrlshortnerApplication.class, args);
	}

}
