package com.shemuel.timeline;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFileStorage
@SpringBootApplication
public class TimelineJavaApplication {

    // & 'C:\Program Files\Java\jdk-1.8\bin\java.exe' '-cp' 'C:\Users\12419\AppData\Local\Temp\cp_dk88gjb7wbtyoa9megxs3nszu.jar' 'com.shemuel.timeline.TimelineJavaApplication' --spring.profiles.active=dev
    public static void main(String[] args) {
        SpringApplication.run(TimelineJavaApplication.class, args);
    }

}
