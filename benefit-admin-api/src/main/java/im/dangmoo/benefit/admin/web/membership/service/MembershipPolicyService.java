package im.dangmoo.benefit.admin.web.membership.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.membership.model.MembershipPolicyRequest;
import im.dangmoo.benefit.admin.web.membership.model.MembershipPolicyResponse;
import im.dangmoo.benefit.admin.web.membership.model.MembershipPolicySearchRequest;
import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicyRepository;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeCacheStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipPolicyService {

    private final MembershipPolicyRepository membershipPolicyRepository;
    private final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore;

    public MembershipPolicyService(
        final MembershipPolicyRepository membershipPolicyRepository,
        final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore
    ) {
        this.membershipPolicyRepository = membershipPolicyRepository;
        this.membershipPrivilegeCacheStore = membershipPrivilegeCacheStore;
    }

    public List<MembershipPolicyResponse> list(final MembershipPolicySearchRequest request) {
        return membershipPolicyRepository.findAll(request.version()).stream()
            .map(MembershipPolicyResponse::of)
            .toList();
    }

    public MembershipPolicyResponse get(final String policyId) {
        final MembershipPolicy policy = membershipPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        return MembershipPolicyResponse.of(policy);
    }

    public MembershipPolicyResponse create(final String adminId, final MembershipPolicyRequest request) {
        if (MembershipPrivilege.of(request.version()) == null) {
            throw new ApiException(ApiMessage.PRIVILEGE_NOT_FOUND);
        }
        if (membershipPolicyRepository.existsByVersion(request.version())) {
            throw new ApiException(ApiMessage.DUPLICATE_VERSION);
        }
        final MembershipPolicy saved = membershipPolicyRepository.save(request.toEntity(adminId));
        membershipPrivilegeCacheStore.put(saved);
        return MembershipPolicyResponse.of(saved);
    }

    public MembershipPolicyResponse update(
        final String adminId,
        final String policyId,
        final MembershipPolicyRequest request
    ) {
        final MembershipPolicy policy = membershipPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (MembershipPrivilege.of(request.version()) == null) {
            throw new ApiException(ApiMessage.PRIVILEGE_NOT_FOUND);
        }
        if (policy.getVersion() != request.version() && membershipPolicyRepository.existsByVersion(request.version())) {
            throw new ApiException(ApiMessage.DUPLICATE_VERSION);
        }
        final MembershipPolicy saved = membershipPolicyRepository.save(policy.update(
            request.version(),
            MembershipPrivilege.of(request.version()),
            request.period() == null ? null : request.period().toEntity(),
            adminId
        ));
        membershipPrivilegeCacheStore.put(saved);
        return MembershipPolicyResponse.of(saved);
    }
}
