package com.devops.certtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CertTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertTrackerApplication.class, args);
	}

}
