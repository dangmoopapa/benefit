package im.dangmoo.benefit.domain.point;

public final class PointUsageDomain {

    public enum Usability {
        USABLE,
        INVALID_AMOUNT,
        INSUFFICIENT_BALANCE
    }

    private final long availableAmount;

    private PointUsageDomain(final long availableAmount) {
        this.availableAmount = availableAmount;
    }

    public static PointUsageDomain of(final long availableAmount) {
        return new PointUsageDomain(availableAmount);
    }

    public Usability usabilityOf(final long amount) {
        if (amount <= 0) {
            return Usability.INVALID_AMOUNT;
        }
        if (amount > availableAmount) {
            return Usability.INSUFFICIENT_BALANCE;
        }
        return Usability.USABLE;
    }
}
