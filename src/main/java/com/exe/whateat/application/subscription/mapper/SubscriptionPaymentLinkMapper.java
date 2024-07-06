package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.SubscriptionPaymentLinkResponse;
import com.exe.whateat.infrastructure.payos.request.PayOSPaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPaymentLinkMapper implements WhatEatMapper<PayOSPaymentResponse, SubscriptionPaymentLinkResponse> {

    @Override
    public SubscriptionPaymentLinkResponse convertToDto(PayOSPaymentResponse payOSPaymentResponse) {
        if (payOSPaymentResponse == null) {
            return null;
        }
        return SubscriptionPaymentLinkResponse.builder()
                .paymentLinkId(payOSPaymentResponse.getData().getPaymentLinkId())
                .checkoutUrl(payOSPaymentResponse.getData().getCheckoutUrl())
                .qrCode(payOSPaymentResponse.getData().getQrCode())
                .build();
    }
}
