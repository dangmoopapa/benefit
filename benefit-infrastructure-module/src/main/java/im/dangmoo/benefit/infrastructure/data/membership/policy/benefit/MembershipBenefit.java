package im.dangmoo.benefit.infrastructure.data.membership.policy.benefit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Season1MembershipBenefit.class, name = "SEASON_1"),
    @JsonSubTypes.Type(value = Season2MembershipBenefit.class, name = "SEASON_2")
})
public abstract class MembershipBenefit {
}
