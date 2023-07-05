package br.inatel.conversionmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ConversionManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConversionManagerApplication.class, args);
	}

}
