package edu.ysu.officeque;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OfficeQueApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfficeQueApplication.class, args);
    }

    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
