package com.fatec.runetasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RunetasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunetasksApplication.class, args);
	}

}
