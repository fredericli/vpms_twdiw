package example.vpms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VpmsApplication.class, args);
    }
} 