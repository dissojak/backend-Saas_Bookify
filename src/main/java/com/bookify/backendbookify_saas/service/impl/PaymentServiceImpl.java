package com.bookify.backendbookify_saas.service.impl;

import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentRequest;
import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.FlouciVerifyPaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.PaymentCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Booking;
import com.bookify.backendbookify_saas.models.entities.Payment;
import com.bookify.backendbookify_saas.models.entities.Subscription;
import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import com.bookify.backendbookify_saas.models.enums.SubscriptionPlan;
import com.bookify.backendbookify_saas.models.enums.SubscriptionStatus;
import com.bookify.backendbookify_saas.repositories.BookingRepository;
import com.bookify.backendbookify_saas.repositories.PaymentRepository;
import com.bookify.backendbookify_saas.repositories.SubscriptionRepository;
import com.bookify.backendbookify_saas.service.FlouciPaymentService;
import com.bookify.backendbookify_saas.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final FlouciPaymentService flouciPaymentService;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(FlouciPaymentService flouciPaymentService,
                              PaymentRepository paymentRepository,
                              SubscriptionRepository subscriptionRepository,
                              BookingRepository bookingRepository) {
        this.flouciPaymentService = flouciPaymentService;
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public FlouciGeneratePaymentResponse createPaymentAndLog(PaymentCreateRequest request) {
        // build Flouci request
        FlouciGeneratePaymentRequest fr = new FlouciGeneratePaymentRequest(
                request.getAmount() != null ? request.getAmount().longValue() : null,
                request.getCurrency(),
                "true",
                1200,
                request.getSuccessLink(),
                request.getFailLink(),
                request.getDeveloperTrackingId()
        );

        FlouciGeneratePaymentResponse response = flouciPaymentService.generatePayment(fr);

        // persist payment log
        try {
            Payment p = new Payment();
            p.setAmount(request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO);
            p.setMethod("FLOUCI");
            p.setStatus(response.getStatus() != null ? response.getStatus() : "PENDING");
            p.setTransactionRef(response.getPaymentId());

            if (request.getSubscriptionId() != null) {
                Optional<Subscription> sub = subscriptionRepository.findById(request.getSubscriptionId());
                sub.ifPresent(p::setSubscription);
            }

            paymentRepository.save(p);
        } catch (Exception e) {
            log.error("Failed to save payment log", e);
        }

        return response;
    }

    @Override
    @Transactional
    public FlouciVerifyPaymentResponse verifyPaymentAndProcess(String paymentId, Long subscriptionId, Long bookingId) {
        FlouciVerifyPaymentResponse response = flouciPaymentService.verifyPayment(paymentId);

        // find existing payment by transaction ref or create new
        Payment payment = paymentRepository.findByTransactionRef(paymentId).orElseGet(() -> {
            Payment p = new Payment();
            p.setTransactionRef(paymentId);
            p.setMethod("FLOUCI");
            p.setAmount(response.getAmount() != null ? new BigDecimal(response.getAmount()) : BigDecimal.ZERO);
            return p;
        });

        payment.setStatus(response.getStatus());
        paymentRepository.save(payment);

        // If related to subscription and successful, activate subscription
        if (subscriptionId != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
            Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);
            if (optSub.isPresent()) {
                Subscription sub = optSub.get();
                sub.setStatus(SubscriptionStatus.ACTIVE);
                LocalDate start = LocalDate.now();
                sub.setStartDate(start);
                int days = planToDays(sub.getPlan());
                if (days > 0) {
                    sub.setEndDate(start.plusDays(days));
                }
                subscriptionRepository.save(sub);

                // link payment to subscription
                payment.setSubscription(sub);
                paymentRepository.save(payment);
            } else {
                log.warn("Subscription id {} not found while processing payment {}", subscriptionId, paymentId);
            }
        }

        // If related to booking and successful, update booking status and link payment
        if (bookingId != null && "SUCCESS".equalsIgnoreCase(response.getStatus())) {
            Optional<Booking> optBooking = bookingRepository.findById(bookingId);
            if (optBooking.isPresent()) {
                Booking booking = optBooking.get();
                booking.setStatus(BookingStatusEnum.COMPLETED);
                bookingRepository.save(booking);

                payment.setBooking(booking);
                paymentRepository.save(payment);
            } else {
                log.warn("Booking id {} not found while processing payment {}", bookingId, paymentId);
            }
        }

        return response;
    }

    @Override
    public Payment findByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef).orElse(null);
    }

    // Map plan to days - assumption, adapt as needed
    private int planToDays(SubscriptionPlan plan) {
        if (plan == null) return 0;
        switch (plan) {
            case FREE:
                return 0;
            case BASIC:
                return 30;
            case PRO:
                return 90;
            case PREMIUM:
                return 365;
            case ENTERPRISE:
                return 3650;
            default:
                return 0;
        }
    }
}
