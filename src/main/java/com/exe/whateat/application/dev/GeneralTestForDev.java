package com.exe.whateat.application.dev;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.x4ala1c.tsid.Tsid;
import io.github.x4ala1c.tsid.TsidGenerator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
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

    public record ImageRequest(String image) {

    }

    @Data
    @Builder
    public static final class JakartaValidationRequest {

        @NotBlank(message = "Must not be blank.")
        private String notNull;

        @NotNull(message = "Number required.")
        @Min(value = 0, message = "Must be non-negative.")
        private Integer notNegative;
    }

    @Profile("dev")
    @RestController
    @RequiredArgsConstructor
    public static final class Controller extends AbstractController {

        private static final String CHECKSUM_KEY = "1a54716c8f0efb2744fb28b6e38b25da7f67a925d98bc1c18bd8faaecadd7675";
        private static final String REQUEST_BODY = "{'orderCode':123,'amount':3000,'description':'VQRIO123','accountNumber':'12345678','reference':'TF230204212323','transactionDateTime':'2023-02-04 18:25:00','currency':'VND','paymentLinkId':'124c33293c43417ab7879e14c8d9eb18','code':'00','desc':'Thành công','counterAccountBankId':'','counterAccountBankName':'','counterAccountName':'','counterAccountNumber':'','virtualAccountName':'','virtualAccountNumber':''}";
        private static final String SIGNATURE = "412e915d2871504ed31be63c8f62a149a4410d34c4c42affc9006ef9917eaa03";

        private final WhatEatSecurityHelper securityHelper;
        private final FirebaseImageService firebaseImageService;
        private final ObjectMapper objectMapper;

        @Operation(hidden = true)
        @PostMapping("/test/untrimmed-string-body")
        public ResponseEntity<String> untrimmedStringTest(@RequestBody TestRequestBodyStringUntrimmedRequest request) {
            return ResponseEntity.ok(request.value);
        }

        @Operation(hidden = true)
        @GetMapping("/test/tsid/generate")
        public ResponseEntity<Object> generateTsid(@RequestParam(defaultValue = "10") Integer amount,
                                                   @RequestParam(defaultValue = "false") boolean longOnly) {
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
            if (longOnly) {
                StringBuilder builder = new StringBuilder();
                for (Tsid tsid : tsids) {
                    builder.append('\'');
                    builder.append(tsid.asLong());
                    builder.append("\n");
                }
                return ResponseEntity.ok(builder.toString());
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

        @Operation(hidden = true)
        @PostMapping("/test/validation")
        @SuppressWarnings("unused")
        public ResponseEntity<JakartaValidationRequest> testJakartaValidation(
                @Valid @RequestBody JakartaValidationRequest request) {
            return ResponseEntity.ok(request);
        }

        @Operation(hidden = true)
        @PostMapping("/test/image")
        public ResponseEntity<FirebaseImageResponse> testUploadImage(@RequestBody ImageRequest request) {
            return ResponseEntity.ok(firebaseImageService.uploadBase64Image(request.image()));
        }

        @Operation(hidden = true)
        @GetMapping("/test/pagination")
        public ResponseEntity<PaginationRequest> testUploadImage(@Valid PaginationRequest request) {
            return ResponseEntity.ok(request);
        }

        @Operation(hidden = true)
        @GetMapping("/test/payos/done")
        public ResponseEntity<String> testPayOSDone() {
            return ResponseEntity.ok("Payment is done!!!");
        }

        @Operation(hidden = true)
        @GetMapping("/test/payos/cancel")
        public ResponseEntity<String> testPayOSCancel() {
            return ResponseEntity.ok("Payment is cancelled!!!");
        }

        @Operation(hidden = true)
        @GetMapping("/test/payos/signature")
        public ResponseEntity<Boolean> testPayOSSignature() throws JsonProcessingException {
            return ResponseEntity.ok(StringUtils.equals(calculateSignature(), SIGNATURE));
        }

        private String calculateSignature() throws JsonProcessingException {
            final JsonNode jsonNode = objectMapper.readTree(REQUEST_BODY.replace('\'', '\"'));
            final Iterator<String> keyIterator = createKeyIterator(jsonNode);
            final StringBuilder builder = new StringBuilder();
            while (keyIterator.hasNext()) {
                final String key = keyIterator.next();
                final String value = jsonNode.get(key).asText();
                builder.append(key);
                builder.append('=');
                builder.append(value);
                if (keyIterator.hasNext()) {
                    builder.append('&');
                }
            }
            return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, CHECKSUM_KEY).hmacHex(builder.toString());
        }

        private Iterator<String> createKeyIterator(JsonNode jsonNode) {
            final List<String> keys = new ArrayList<>();
            final Iterator<String> keyIterator = jsonNode.fieldNames();
            while (keyIterator.hasNext()) {
                keys.add(keyIterator.next());
            }
            return keys.stream().sorted(String::compareTo).toList().iterator();
        }
    }
}
