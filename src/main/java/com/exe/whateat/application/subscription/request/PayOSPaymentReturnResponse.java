package com.exe.whateat.application.subscription.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayOSPaymentReturnResponse {

    public enum Status {

        PAID,
        PENDING,
        PROCESSING,
        CANCELLED
    }

    private String code;
    private String id;
    private boolean cancel;
    private Status status;
    private int orderCode;
}
