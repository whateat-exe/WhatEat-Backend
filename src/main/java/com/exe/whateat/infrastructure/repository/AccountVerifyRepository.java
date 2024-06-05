package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.account.AccountVerify;
import com.exe.whateat.entity.account.VerificationStatus;
import com.exe.whateat.entity.common.WhatEatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountVerifyRepository extends JpaRepository<AccountVerify, WhatEatId> {

    /**
     * The status is hard-coded, so if there's any change to {@link VerificationStatus}, remember to check this!
     *
     * @param id The Account's ID.
     */
    @Modifying
    @Query(
            value = """
                    UPDATE AccountVerify SET status = 'EXPIRED'
                    WHERE account.id = ?1
                    """
    )
    void invalidateAllPreviousVerificationCodes(WhatEatId id);

    @Query(
            value = """
                    SELECT av FROM AccountVerify av
                    WHERE av.account.id = ?1 AND av.id <= ?2 AND av.status = 'PENDING'
                    ORDER BY av.id DESC
                    LIMIT 1
                    """
    )
    Optional<AccountVerify> findRecentVerificationCode(WhatEatId accountId, WhatEatId maximumId);

    @Query(
            value = """
                    UPDATE AccountVerify av SET av.status = 'VERIFIED'
                    WHERE av.account.id = ?1 AND av.verificationCode = ?2
                    """
    )
    @Modifying
    void updateTheCodeToBeVerified(WhatEatId accountId, String verificationCode);

    @Query(value = """
                    update account_verify as av set status = 'EXPIRED'
                    where status = 'PENDING'
                    and  (?1 - (av.id >> 22))  > ?2
                   """, nativeQuery = true)
    @Modifying
    void updateTheCodeToExpired(Long present, Long exceed);

    @Query(value = """
                     delete from account_verify
                     where status = 'EXPIRED'
                    """, nativeQuery = true)
    @Modifying
    void deleteAllCodeExpired();

    @Query(value = """
                     delete from account_verify
                     where id in ?1
                    """, nativeQuery = true)
    @Modifying
    void deleteAllCodePendingUnused(List<Long> ids);
}
