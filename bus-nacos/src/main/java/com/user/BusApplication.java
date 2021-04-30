package com.user;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
public class BusApplication {

//    @Value("${server.port}")
//    private String port;
//
//    @PostConstruct
//    public void say() {
//        System.out.println(port);
//    }


    public static void main(String[] args) {

        SpringApplication.run(BusApplication.class, args);

        //new SpringApplicationBuilder(BusApplication.class).properties("server.port=" + value).run(args);


    }

}
