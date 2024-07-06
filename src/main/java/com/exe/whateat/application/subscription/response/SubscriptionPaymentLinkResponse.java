package com.exe.whateat.application.subscription.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPaymentLinkResponse {

    private String paymentLinkId;
    private String checkoutUrl;
    private String qrCode;
}
