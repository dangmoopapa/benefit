package im.dangmoo.benefit.domain;

import im.dangmoo.benefit.data.BenefitDataModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BenefitDataModule.class)
public class BenefitDomainModule {
}
