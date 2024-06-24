package com.exe.whateat.infrastructure.service.account.verification;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountVerify;
import com.exe.whateat.entity.account.VerificationStatus;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.email.WhatEatEmailService;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import io.github.x4ala1c.tsid.Tsid;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@Transactional(rollbackOn = Exception.class)
public class AccountVerificationServiceImpl implements AccountVerificationService {

    private static final String EMAIL_TITLE = "Xác thực tài khoản WhatEat - %s";
    private static final String EMAIL_BODY = """
            <html>
                <body>
                    <p>Chào mừng bạn đến với WhatEat!</p>
                    <br />
                    <p>Hãy lướt qua lại một vài thông tin cơ bản nhé:</p>
                    <ul>
                        <li>ID: %s</li>
                        <li>Email: %s</li>
                        <li>Tên đầy đủ: %s</li>
                        <li>Số điện thoại: %s</li>
                    </ul>
                    <br />
                    <p>Để tiếp tục trải nghiệm mọi thứ mà ứng dụng chúng tôi cung cấp, mời bạn nhập mã xác thực sau đây vào
                    ứng dụng của chúng tôi:</p>
                    <br />
                    <p><strong>Mã xác thực: %s</strong></p>
                    <br />
                    <p>Cảm ơn các bạn đã tải và sử dụng ứng dụng WhatEat!</p>
                </body>
            </html>
            """;

    private static final String EMAIL_TITLE_VERIFICATION_SUCCESS = "Xác thực tài khoản WhatEat thành công";
    private static final String EMAIL_BODY_VERIFICATION_SUCCESS = """
                <html>
                <body>
                    <p>Chào mừng bạn đến với WhatEat!</p>
                    <br />
                    <p>Hãy lướt qua lại một vài thông tin cơ bản nhé:</p>
                    <ul>
                        <li>ID: %s</li>
                        <li>Email: %s</li>
                        <li>Tên đầy đủ: %s</li>
                        <li>Số điện thoại: %s</li>
                    </ul>
                    <br />
                    <p>Bạn đã xác thực tài khoản thành công!</p>
                    <br />
                    <p>Cảm ơn các bạn đã tải và sử dụng ứng dụng WhatEat!</p>
                </body>
            </html>
            """;

    private static final String EMAIL_TITLE_ACTIVATION = "Thông báo WhatEat";
    private static final String EMAIL_BODY_ACTIVATION = """
                <html>
                <body>
                    <p>Một vài thông tin cơ bản:</p>
                    <ul>
                        <li>ID: %s</li>
                        <li>Email: %s</li>
                        <li>Tên đầy đủ: %s</li>
                        <li>Số điện thoại: %s</li>
                    </ul>
                    <br />
                    <p>Tài khoản của bạn đã được kích hoạt.</p>
                    <br />
                    <p>Cảm ơn các bạn đã tải và sử dụng ứng dụng WhatEat!</p>
                </body>
            </html>
            """;

    private static final String EMAIL_BODY_DEACTIVATION = """
                <html>
                <body>
                    <p>Một vài thông tin cơ bản:</p>
                    <ul>
                        <li>ID: %s</li>
                        <li>Email: %s</li>
                        <li>Tên đầy đủ: %s</li>
                        <li>Số điện thoại: %s</li>
                    </ul>
                    <br />
                    <p>Tài khoản của bạn đã bị vô hiệu hoá.</p>
                    <br />
                    <p>Cảm ơn các bạn đã tải và sử dụng ứng dụng WhatEat!</p>
                </body>
            </html>
            """;

    private static final String EMAIL_BODY_REGISTER_RESTAURANT = """
                <html>
                <body>
                    <p>Chào mừng bạn đến với WhatEat!</p>
                    <br />
                    <p>Hãy lướt qua lại một vài thông tin cơ bản nhé:</p>
                    <ul>
                        <li>ID: %s</li>
                        <li>Email: %s</li>
                        <li>Tên đầy đủ: %s</li>
                        <li>Số điện thoại: %s</li>
                    </ul>
                    <br />
                    <p>Tài khoản của bạn đang trong trạng thái pending, vui lòng chờ Admin duyệt.</p>
                    <br />
                    <p>Cảm ơn các bạn đã tải và sử dụng ứng dụng WhatEat!</p>
                </body>
            </html>
            """;

    private static final long VERIFICATION_DURATION = 15L * 60;

    private final AccountVerifyRepository accountVerifyRepository;
    private final WhatEatEmailService whatEatEmailService;
    private final AccountRepository accountRepository;

    @Value("${whateat.tsid.epoch}")
    private long tsidEpoch;

    @Autowired
    public AccountVerificationServiceImpl(AccountVerifyRepository accountVerifyRepository,
                                          WhatEatEmailService whatEatEmailService,
                                          AccountRepository accountRepository) {
        this.accountVerifyRepository = accountVerifyRepository;
        this.whatEatEmailService = whatEatEmailService;
        this.accountRepository = accountRepository;
    }

    @Override
    public void verifyAccount(Account account, String verificationCode) {
        validateAccount(account);
        if (account.getStatus() != ActiveStatus.PENDING) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEA_0013)
                    .reason("account", "Tài khoản không trong trạng thái cần xác thực.")
                    .build();
        }
        final long maximumTimestamp = Instant.now().toEpochMilli() - tsidEpoch + VERIFICATION_DURATION;
        final WhatEatId maximumId = new WhatEatId(Tsid.fromLong(maximumTimestamp << 22));
        final AccountVerify accountVerify = accountVerifyRepository.findRecentVerificationCode(account.getId(), maximumId)
                .orElseThrow(() -> WhatEatException.builder()
                        .code(WhatEatErrorCode.WEA_0012)
                        .reason("verificationCode", "Mã xác thực không hợp lệ.")
                        .build());
        if (!Objects.equals(accountVerify.getVerificationCode(), verificationCode)) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEA_0012)
                    .reason("verificationCode", "Mã xác thực không hợp lệ.")
                    .build();
        }
        accountVerifyRepository.updateTheCodeToBeVerified(account.getId(), verificationCode);
        account.setStatus(ActiveStatus.ACTIVE);
        accountRepository.save(account);

        final String emailBody = String.format(EMAIL_BODY_VERIFICATION_SUCCESS, account.getId(), account.getEmail(), account.getFullName(),
                account.getPhoneNumber());
        whatEatEmailService.sendMail(account.getEmail(), emailBody, String.format(EMAIL_TITLE_VERIFICATION_SUCCESS));
    }

    @Override
    public void resendVerificationCode(Account account) {
        validateAccount(account);
        accountVerifyRepository.invalidateAllPreviousVerificationCodes(account.getId());
        this.sendVerificationCode(account);
    }

    @Override
    public void sendVerificationCode(Account account) {
        validateAccount(account);
        final String code = VerificationCodeGenerator.generate();
        final AccountVerify accountVerify = AccountVerify.builder()
                .id(WhatEatId.generate())
                .verificationCode(code)
                .status(VerificationStatus.PENDING)
                .account(account)
                .build();
        accountVerifyRepository.save(accountVerify);
        final String emailBody = String.format(EMAIL_BODY, account.getId(), account.getEmail(), account.getFullName(),
                account.getPhoneNumber(), code);
        whatEatEmailService.sendMail(account.getEmail(), emailBody, String.format(EMAIL_TITLE, account.getEmail()));
    }

    @Override
    public void sendActivatingAccountEmail(Account account) {
        validateAccount(account);
        final String emailBody = String.format(EMAIL_BODY_ACTIVATION, account.getId(), account.getEmail(), account.getFullName(),
                account.getPhoneNumber());
        whatEatEmailService.sendMail(account.getEmail(), emailBody, String.format(EMAIL_TITLE_ACTIVATION));
    }

    @Override
    public void sendDeactivatingAccountEmail(Account account) {
        validateAccount(account);
        final String emailBody = String.format(EMAIL_BODY_DEACTIVATION, account.getId(), account.getEmail(), account.getFullName(),
                account.getPhoneNumber());
        whatEatEmailService.sendMail(account.getEmail(), emailBody, String.format(EMAIL_TITLE_ACTIVATION));
    }

    @Override
    public void sendRegisterRestaurantEmail(Account account) {
        validateAccount(account);
        final String emailBody = String.format(EMAIL_BODY_REGISTER_RESTAURANT, account.getId(), account.getEmail(), account.getFullName(),
                account.getPhoneNumber());
        whatEatEmailService.sendMail(account.getEmail(), emailBody, String.format(EMAIL_TITLE_ACTIVATION));
    }

    private static void validateAccount(Account account) {
        if (account == null) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEV_0005)
                    .reason("account", "Tài khoản là bắt buộc.")
                    .build();
        }
    }
}
