package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.account.AccountVerify;
import com.exe.whateat.entity.account.VerificationStatus;
import com.exe.whateat.entity.common.WhatEatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
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
                    UPDATE AccountVerify av SET av.status = 'EXPIRED'
                    WHERE av.id < ?1
                   """)
    @Modifying
    void updateTheCodeToExpired(WhatEatId id);

    @Query(
            value = """
                    SELECT av FROM AccountVerify av
                    WHERE av.id < ?1 AND av.status = 'PENDING'
                    """
    )
    List<AccountVerify> findPendingCode(WhatEatId maximumId);

    List<AccountVerify> findAllByStatus(VerificationStatus status);
}
