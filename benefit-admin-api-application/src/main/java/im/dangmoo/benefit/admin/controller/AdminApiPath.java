package im.dangmoo.benefit.admin.controller;

public final class AdminApiPath {

    public static final String COUPON_POLICIES = "/coupon/policies";
    public static final String COUPON_POLICY = "/coupon/policies/{id}";
    public static final String COUPON_POLICY_STATUS = "/coupon/policies/{id}/status";
    public static final String COUPON_VOUCHER_POLICIES = "/coupon/policies/vouchers";

    public static final String COUPON_WALLETS = "/coupon/wallets";
    public static final String COUPON_WALLET_USAGE = "/coupon/wallets/{id}/usage";
    public static final String COUPON_WALLET_RECOVERY = "/coupon/wallets/{id}/recovery";

    public static final String POINT_POLICIES = "/point/policies";
    public static final String POINT_POLICY = "/point/policies/{id}";
    public static final String POINT_POLICY_STATUS = "/point/policies/{id}/status";

    public static final String POINT_TRANSACTIONS = "/point/transactions";
    public static final String POINT_TRANSACTION_GRANT = "/point/transactions/grant";
    public static final String POINT_TRANSACTION_RECLAIM = "/point/transactions/reclaim";
    public static final String POINT_TRANSACTION_USAGE = "/point/transactions/usage";
    public static final String POINT_TRANSACTION_RECOVERY = "/point/transactions/recovery";

    public static final String MEMBERSHIP_POLICIES = "/membership/policies";
    public static final String MEMBERSHIP_POLICY = "/membership/policies/{id}";
    public static final String MEMBERSHIP_POLICY_STATUS = "/membership/policies/{id}/status";
    public static final String MEMBERSHIP_CONTRACTS = "/membership/contracts";
    public static final String MEMBERSHIP_CONTRACT_JOIN = "/membership/contracts/join";
    public static final String MEMBERSHIP_CONTRACT_LEAVE = "/membership/contracts/leave";
    public static final String MEMBERSHIP_CONTRACT_RENEW = "/membership/contracts/renew";
    public static final String MEMBERSHIP_BENEFIT_HISTORIES = "/membership/benefit/histories";

    private AdminApiPath() {
    }
}
