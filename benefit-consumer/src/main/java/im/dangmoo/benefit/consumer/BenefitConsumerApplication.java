package im.dangmoo.benefit.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "im.dangmoo.benefit")
public class BenefitConsumerApplication {

    static void main(String[] args) {
        SpringApplication.run(BenefitConsumerApplication.class, args);
    }
}
