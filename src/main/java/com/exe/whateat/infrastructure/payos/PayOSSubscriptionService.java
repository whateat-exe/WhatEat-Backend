package com.exe.whateat.infrastructure.payos;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.SubscriptionService;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.Money;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.entity.subscription.PaymentProvider;
import com.exe.whateat.entity.subscription.PaymentStatus;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import com.exe.whateat.entity.subscription.SubscriptionStatus;
import com.exe.whateat.entity.subscription.UserSubscription;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import com.exe.whateat.infrastructure.payos.request.PayOSPaymentRequest;
import com.exe.whateat.infrastructure.payos.request.PayOSPaymentResponse;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionRepository;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class PayOSSubscriptionService implements SubscriptionService {

    private static final String CLIENT_ID_HEADER = "x-client-id";
    private static final String API_KEY_HEADER = "x-api-key";

    private static final String CREATE_PAYMENT_URL = "https://api-merchant.payos.vn/v2/payment-requests";
    private static final String CANCEL_PAYMENT_URL = "https://api-merchant.payos.vn/v2/payment-requests/%s/cancel";

    private static final String CREATE_PAYMENT_SIGNATURE_FORMAT =
            "amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s";

    private final RestaurantSubscriptionRepository subscriptionRepository;
    private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;
    private final RestTemplate restTemplate;

    @Value("${whateat.payos.clientid}")
    private String clientId;

    @Value("${whateat.payos.apikey}")
    private String apiKey;

    @Value("${whateat.payos.checksumkey}")
    private String checksumKey;

    @Value("${whateat.payos.return-url}")
    private String returnUrl;

    @Value("${whateat.payos.cancel-url}")
    private String cancelUrl;

    @Override
    public PayOSPaymentResponse subscribeRestaurant(Restaurant restaurant, RestaurantSubscriptionType subscriptionType) {
        final RestaurantSubscription subscription = subscriptionRepository.findByType(subscriptionType)
                .orElseThrow(() -> WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("subscriptionType", "Loại gói không hợp lệ.")
                        .build());
        final PayOSPaymentResponse paymentResponse = createRestaurantPaymentRequest(restaurant, subscription);
        try {
            final RestaurantSubscriptionTracker tracker = RestaurantSubscriptionTracker.builder()
                    .id(WhatEatId.generate())
                    .amount(Money.of(paymentResponse.getData().getAmount()))
                    .signature(paymentResponse.getSignature())
                    .paymentId(paymentResponse.getData().getPaymentLinkId())
                    .orderCode(paymentResponse.getData().getOrderCode())
                    .paymentStatus(PaymentStatus.PENDING)
                    .provider(PaymentProvider.PAY_OS)
                    .subscriptionStatus(SubscriptionStatus.PENDING)
                    .subscription(subscription)
                    .restaurant(restaurant)
                    .build();
            restaurantSubscriptionTrackerRepository.save(tracker);
        } catch (Exception e) {
            cancelPayment(paymentResponse.getData().getPaymentLinkId());
            throw e;
        }
        return paymentResponse;
    }

    @Override
    public PayOSPaymentResponse subscribeUser(Account account) {
        if (userSubscriptionTrackerRepository.userIsUnderActiveSubscription(account.getId())) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEB_0020)
                    .reason("subscription", "Tài khoản đã đăng ký gói trước đó")
                    .build();
        }
        final UserSubscription subscription = userSubscriptionRepository.findFirstBy()
                .orElseThrow(() -> WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("type", "Loại gói không hợp lệ.")
                        .build());
        final PayOSPaymentResponse paymentResponse = createUserPaymentRequest(account, subscription);
        try {
            final UserSubscriptionTracker tracker = UserSubscriptionTracker.builder()
                    .id(WhatEatId.generate())
                    .amount(Money.of(paymentResponse.getData().getAmount()))
                    .signature(paymentResponse.getSignature())
                    .paymentId(paymentResponse.getData().getPaymentLinkId())
                    .orderCode(paymentResponse.getData().getOrderCode())
                    .paymentStatus(PaymentStatus.PENDING)
                    .provider(PaymentProvider.PAY_OS)
                    .subscriptionStatus(SubscriptionStatus.PENDING)
                    .subscription(subscription)
                    .user(account)
                    .build();
            userSubscriptionTrackerRepository.save(tracker);
        } catch (Exception e) {
            cancelPayment(paymentResponse.getData().getPaymentLinkId());
            throw e;
        }
        return paymentResponse;
    }

    private PayOSPaymentResponse createUserPaymentRequest(Account account, UserSubscription subscription) {
        final PayOSPaymentRequest request = PayOSPaymentRequest.builder()
                .orderCode(userSubscriptionTrackerRepository.generateOrderCode())
                .amount(subscription.getPrice().getAmount().longValue())
                .description(subscription.getName())
                .items(List.of(PayOSPaymentRequest.Item.builder()
                        .name(subscription.getName())
                        .price(subscription.getPrice().getAmount().intValue())
                        .quantity(1)
                        .build()))
                .buyerName(account.getFullName())
                .buyerPhone(account.getPhoneNumber())
                .buyerEmail(account.getEmail())
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .expiredAt((int) Instant.now().plus(5, ChronoUnit.MINUTES).getEpochSecond())
                .build();
        return fireRequestToPayOS(request);
    }

    private PayOSPaymentResponse createRestaurantPaymentRequest(Restaurant restaurant, RestaurantSubscription subscription) {
        final PayOSPaymentRequest request = PayOSPaymentRequest.builder()
                .orderCode(restaurantSubscriptionTrackerRepository.generateOrderCode())
                .amount(subscription.getPrice().getAmount().longValue())
                .description(subscription.getType().name())
                .items(List.of(PayOSPaymentRequest.Item.builder()
                        .name(subscription.getName())
                        .price(subscription.getPrice().getAmount().intValue())
                        .quantity(1)
                        .build()))
                .buyerAddress(restaurant.getAddress())
                .buyerName(restaurant.getName())
                .buyerPhone(restaurant.getAccount().getPhoneNumber())
                .buyerEmail(restaurant.getAccount().getEmail())
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .expiredAt((int) Instant.now().plus(5, ChronoUnit.MINUTES).getEpochSecond())
                .build();
        return fireRequestToPayOS(request);
    }

    private PayOSPaymentResponse fireRequestToPayOS(PayOSPaymentRequest request) {
        final String signature = calculateSignature(request);
        request.setSignature(signature);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(CLIENT_ID_HEADER, clientId);
        headers.add(API_KEY_HEADER, apiKey);
        final ResponseEntity<PayOSPaymentResponse> responseEntity = restTemplate.exchange(CREATE_PAYMENT_URL,
                HttpMethod.POST, new HttpEntity<>(request, headers), PayOSPaymentResponse.class);
        if (responseEntity.getStatusCode().isError() || !responseEntity.hasBody()) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("payos", "Lỗi thanh toán bên payOS.")
                    .build();
        }
        final PayOSPaymentResponse response = Objects.requireNonNull(responseEntity.getBody());
        if (response.getData() == null) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("payos", "Lỗi thanh toán bên payOS.")
                    .build();
        }
        response.setSignature(signature);
        return response;
    }

    private String calculateSignature(PayOSPaymentRequest request) {
        final String signature = String.format(CREATE_PAYMENT_SIGNATURE_FORMAT, request.getAmount(),
                request.getCancelUrl(), request.getDescription(), request.getOrderCode(), request.getReturnUrl());
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, checksumKey).hmacHex(signature);
    }

    private void cancelPayment(String paymentLinkId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(CLIENT_ID_HEADER, clientId);
        headers.add(API_KEY_HEADER, apiKey);
        final ResponseEntity<Void> responseEntity = restTemplate.exchange(String.format(CANCEL_PAYMENT_URL, paymentLinkId),
                HttpMethod.POST, new HttpEntity<>(null, headers), Void.class);
        if (responseEntity.getStatusCode().isError()) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("payos", "Lỗi hủy thanh toán bên PayOS.")
                    .build();
        }
    }
}
