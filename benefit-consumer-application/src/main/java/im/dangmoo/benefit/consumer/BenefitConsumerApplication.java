package im.dangmoo.benefit.consumer;

import im.dangmoo.benefit.domain.BenefitDomainModule;
import im.dangmoo.benefit.data.BenefitDataModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BenefitDomainModule.class, BenefitDataModule.class})
public class BenefitConsumerApplication {

    static void main(String[] args) {
        SpringApplication.run(BenefitConsumerApplication.class, args);
    }
}
