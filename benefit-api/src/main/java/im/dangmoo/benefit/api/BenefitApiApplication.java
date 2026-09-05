package im.dangmoo.benefit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "im.dangmoo.benefit")
public class BenefitApiApplication {

    static void main(String[] args) {
        SpringApplication.run(BenefitApiApplication.class, args);
    }
}
