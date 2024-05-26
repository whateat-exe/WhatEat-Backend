package com.exe.whateat.application.account.verification;

import com.exe.whateat.entity.account.Account;

/**
 * Note that this service won't check whether the Account exists or not.
 */
public interface AccountVerificationService {

    /**
     * Verify and return <code>true</code> if the verification code is valid for the selected account.
     *
     * @param account Account to verify.
     * @param code    The verification code
     */
    void verifyAccount(Account account, String code);

    /**
     * Resend verification code, in case the code is not valid anymore.
     *
     * @param account The Account.
     */
    void resendVerificationCode(Account account);

    /**
     * Resend verification code, in case when the account is newly created.
     *
     * @param account The Account.
     */
    void sendVerificationCode(Account account);
}
