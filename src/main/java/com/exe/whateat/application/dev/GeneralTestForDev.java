package com.exe.whateat.application.dev;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.github.x4ala1c.tsid.TsidGenerator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeneralTestForDev {

    public record TestRequestBodyStringUntrimmedRequest(String value) {

    }

    public record TsidResponse(String string, Long value) {

    }

    public record TsidRequest(Tsid id) {

    }

    @Profile("dev")
    @RestController
    @AllArgsConstructor
    public static final class Controller extends AbstractController {

        private final WhatEatSecurityHelper securityHelper;

        @Operation(hidden = true)
        @PostMapping("/test/untrimmed-string-body")
        public ResponseEntity<String> untrimmedStringTest(@RequestBody TestRequestBodyStringUntrimmedRequest request) {
            return ResponseEntity.ok(request.value);
        }

        @Operation(hidden = true)
        @GetMapping("/test/tsid/generate")
        public ResponseEntity<List<TsidResponse>> generateTsid(@RequestParam(defaultValue = "10") Integer amount) {
            if (amount <= 0) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0003)
                        .reason("amount", "Bruh are you kidding me?")
                        .build();
            }
            final List<Tsid> tsids = new LinkedList<>();
            for (int i = 0; i < amount; i++) {
                tsids.add(TsidGenerator.globalGenerate());
            }
            return ResponseEntity.ok(tsids.stream().map(id -> new TsidResponse(id.asString(), id.asLong())).toList());
        }

        @Operation(hidden = true)
        @GetMapping("/test/tsid")
        public ResponseEntity<Tsid> tsidAsString() {
            return ResponseEntity.ok(TsidGenerator.globalGenerate());
        }

        @Operation(hidden = true)
        @PostMapping("/test/tsid")
        @SuppressWarnings("unused")
        public ResponseEntity<String> receiveTsidAsString(@RequestBody TsidRequest request) {
            return ResponseEntity.ok("Successful TSID deserialization.");
        }

        @Operation(hidden = true)
        @GetMapping("/test/account/current")
        public ResponseEntity<String> getCurrentAccount() {
            final Optional<Account> account = securityHelper.getCurrentLoggedInAccount();
            return account.isPresent()
                    ? ResponseEntity.ok("Account exists.")
                    : ResponseEntity.notFound().build();
        }
    }
}
