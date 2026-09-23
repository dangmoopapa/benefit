package im.dangmoo.benefit.batch;

import im.dangmoo.benefit.domain.BenefitDomainModule;
import im.dangmoo.benefit.data.BenefitDataModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BenefitDomainModule.class, BenefitDataModule.class})
public class BenefitBatchApplication {

    static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(BenefitBatchApplication.class, args);
        System.exit(SpringApplication.exit(context));
    }
}
