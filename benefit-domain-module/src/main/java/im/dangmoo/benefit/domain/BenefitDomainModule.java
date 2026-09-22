package im.dangmoo.benefit.domain;

import im.dangmoo.benefit.infrastructure.BenefitInfrastructureModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BenefitInfrastructureModule.class)
public class BenefitDomainModule {
}
