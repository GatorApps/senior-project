package org.gatorapps.garesearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class,
		org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class
})
public class GaresearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(GaresearchApplication.class, args);
	}

}
