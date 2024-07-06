package com.exe.whateat.infrastructure.payos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class PayOSPaymentRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Item {

        private String name;
        private int quantity;
        private int price;
    }

    private int orderCode;
    private long amount;
    private String description;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String buyerAddress;
    private List<Item> items;
    private String cancelUrl;
    private String returnUrl;
    private int expiredAt;
    private String signature;
}
