package com.practice.samplespringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SamplespringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SamplespringbootApplication.class, args);
	}

}
