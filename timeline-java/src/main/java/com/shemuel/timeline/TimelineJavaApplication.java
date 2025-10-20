package com.shemuel.timeline;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFileStorage
@SpringBootApplication
public class TimelineJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimelineJavaApplication.class, args);
    }

}
