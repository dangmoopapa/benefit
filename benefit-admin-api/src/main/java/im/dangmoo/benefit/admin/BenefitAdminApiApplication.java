package im.dangmoo.benefit.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "im.dangmoo.benefit")
public class BenefitAdminApiApplication {

    static void main(String[] args) {
        SpringApplication.run(BenefitAdminApiApplication.class, args);
    }
}
