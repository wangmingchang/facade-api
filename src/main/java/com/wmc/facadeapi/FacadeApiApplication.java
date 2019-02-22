package com.wmc.facadeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.wmc"})
@SpringBootApplication
public class FacadeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacadeApiApplication.class, args);
    }

}
