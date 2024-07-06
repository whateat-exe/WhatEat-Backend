package com.exe.whateat.infrastructure.payos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSPaymentResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Data {

        private String bin;
        private String accountNumber;
        private String accountName;
        private BigDecimal amount;
        private String description;
        private int orderCode;
        private String currency;
        private String paymentLinkId;
        private String status;
        private String checkoutUrl;
        private String qrCode;
    }

    private String code;
    private String desc;
    private Data data;
    private String signature;
}
