package im.dangmoo.benefit.admin.web;

public final class AdminApiPath {

    public static final String COUPON_POLICIES = "/coupons/policies";
    public static final String COUPON_POLICY = "/coupons/policies/{policyId}";
    public static final String COUPON_POLICY_ACTIVATE = "/coupons/policies/{policyId}/activate";
    public static final String COUPON_POLICY_SUSPEND = "/coupons/policies/{policyId}/suspend";

    public static final String COUPON_WALLETS = "/coupons/wallets";
    public static final String COUPON_WALLETS_BY_POLICY = "/coupons/wallets/by-policy";
    public static final String COUPON_WALLETS_BY_USER = "/coupons/wallets/by-user";
    public static final String COUPON_WALLET_USE = "/coupons/wallets/{walletId}/use";
    public static final String COUPON_WALLET_RECOVER = "/coupons/wallets/{walletId}/recover";

    public static final String POINT_POLICIES = "/points/policies";
    public static final String POINT_POLICY = "/points/policies/{policyId}";
    public static final String POINT_POLICY_ACTIVATE = "/points/policies/{policyId}/activate";
    public static final String POINT_POLICY_SUSPEND = "/points/policies/{policyId}/suspend";

    public static final String POINT_BALANCES = "/points/balances";
    public static final String POINT_TRANSACTIONS = "/points/transactions";
    public static final String POINT_TRANSACTION_ISSUE = "/points/transactions/issue";
    public static final String POINT_TRANSACTION_REVOKE = "/points/transactions/revoke";
    public static final String POINT_TRANSACTION_USE = "/points/transactions/use";
    public static final String POINT_TRANSACTION_RESTORE = "/points/transactions/restore";
}
