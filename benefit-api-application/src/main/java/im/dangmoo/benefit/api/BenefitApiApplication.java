package im.dangmoo.benefit.api;

import im.dangmoo.benefit.domain.BenefitDomainModule;
import im.dangmoo.benefit.infrastructure.BenefitInfrastructureModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BenefitDomainModule.class, BenefitInfrastructureModule.class})
public class BenefitApiApplication {

    static void main(String[] args) {
        SpringApplication.run(BenefitApiApplication.class, args);
    }
}
