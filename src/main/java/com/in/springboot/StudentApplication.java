package com.in.springboot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableAutoConfiguration(exclude = {
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})

@SpringBootApplication
public class StudentApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(
				StudentApplication.class, args);
		//System.out.println(ctx);
	}
}