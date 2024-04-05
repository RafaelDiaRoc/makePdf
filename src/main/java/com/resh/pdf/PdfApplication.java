package com.resh.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("controller")
public class PdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfApplication.class, args);
	}

}
