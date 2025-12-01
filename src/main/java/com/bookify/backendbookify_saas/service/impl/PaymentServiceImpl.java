package com.bookify.backendbookify_saas.service.impl;

import com.bookify.backendbookify_saas.models.dtos.PaymentInitiationResponse;
import com.bookify.backendbookify_saas.models.dtos.PaymentVerificationResponse;
import com.bookify.backendbookify_saas.models.dtos.SubscriptionPaymentRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Payment;
import com.bookify.backendbookify_saas.models.entities.Subscription;
import com.bookify.backendbookify_saas.models.enums.SubscriptionStatus;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.PaymentRepository;
import com.bookify.backendbookify_saas.repositories.SubscriptionRepository;
import com.bookify.backendbookify_saas.service.PaymentGateway;
import com.bookify.backendbookify_saas.service.PaymentGatewayResponse;
import com.bookify.backendbookify_saas.service.PaymentService;
import com.bookify.backendbookify_saas.service.PaymentVerificationResult;
import com.bookify.backendbookify_saas.service.SubscriptionPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Implémentation du service de paiement suivant les principes SOLID
 * - Single Responsibility: gère uniquement la logique métier des paiements
 * - Open/Closed: ouvert à l'extension (nouveaux gateways) sans modification
 * - Dependency Inversion: dépend de l'abstraction PaymentGateway, pas de Flouci directement
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentGateway paymentGateway;
    private final SubscriptionPricingService pricingService;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BusinessRepository businessRepository;

    public PaymentServiceImpl(
            @Qualifier("flouciPaymentGateway") PaymentGateway paymentGateway,
            SubscriptionPricingService pricingService,
            PaymentRepository paymentRepository,
            SubscriptionRepository subscriptionRepository,
            BusinessRepository businessRepository) {
        this.paymentGateway = paymentGateway;
        this.pricingService = pricingService;
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    @Transactional
    public PaymentInitiationResponse initiateSubscriptionPayment(SubscriptionPaymentRequest request) {
        try {
            // 1. Valider que le business existe
            Optional<Business> businessOpt = businessRepository.findById(request.getBusinessId());
            if (!businessOpt.isPresent()) {
                return new PaymentInitiationResponse(null, null, false, "Business not found");
            }

            // 2. Calculer le prix selon le plan (pas depuis le frontend!)
            long amountInMillimes = pricingService.getPriceInMillimes(request.getPlan());
            BigDecimal amount = pricingService.getPrice(request.getPlan());
            int durationDays = pricingService.getDurationInDays(request.getPlan());

            // 3. Créer la subscription en statut PENDING avec les dates définies
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = durationDays > 0 ? startDate.plusDays(durationDays) : null;

            Subscription subscription = new Subscription();
            subscription.setBusiness(businessOpt.get());
            subscription.setPlan(request.getPlan());
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscription.setPrice(amount);
            subscription.setStartDate(startDate);
            subscription.setEndDate(endDate);
            subscription = subscriptionRepository.save(subscription);

            log.info("Created PENDING subscription id={} for business={} plan={}",
                    subscription.getId(), request.getBusinessId(), request.getPlan());

            // 4. Générer le lien de paiement via le gateway
            String successLink = request.getSuccessLink() != null && !request.getSuccessLink().isEmpty()
                    ? request.getSuccessLink()
                    : "http://localhost:3000/subscription/success";
            String failLink = request.getFailLink() != null && !request.getFailLink().isEmpty()
                    ? request.getFailLink()
                    : "http://localhost:3000/subscription/fail";

            // Utiliser l'ID de subscription comme tracking ID
            String trackingId = "SUB-" + subscription.getId();

            PaymentGatewayResponse gatewayResponse = paymentGateway.generatePayment(
                    amountInMillimes,
                    successLink + "?subscriptionId=" + subscription.getId(),
                    failLink + "?subscriptionId=" + subscription.getId(),
                    trackingId
            );

            if (!gatewayResponse.isSuccess()) {
                // Supprimer la subscription si le paiement n'a pas pu être créé
                subscriptionRepository.delete(subscription);
                return new PaymentInitiationResponse(null, null, false, gatewayResponse.getErrorMessage());
            }

            log.info("Payment link generated: paymentId={} for subscription={}",
                    gatewayResponse.getPaymentId(), subscription.getId());

            return new PaymentInitiationResponse(
                    gatewayResponse.getPaymentId(),
                    gatewayResponse.getCheckoutUrl(),
                    true,
                    null
            );

        } catch (Exception e) {
            log.error("Error initiating subscription payment", e);
            return new PaymentInitiationResponse(null, null, false, e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentVerificationResponse verifyAndCompleteSubscriptionPayment(String paymentId, Long subscriptionId) {
        try {
            // 1. Vérifier le paiement via le gateway
            PaymentVerificationResult verificationResult = paymentGateway.verifyPayment(paymentId);

            // 2. Récupérer la subscription
            Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
            if (!subscriptionOpt.isPresent()) {
                log.warn("Subscription {} not found for payment {}", subscriptionId, paymentId);
                return new PaymentVerificationResponse(false, "FAILED", "Subscription not found", null);
            }

            Subscription subscription = subscriptionOpt.get();

            // 3. Créer le log de paiement (TOUJOURS, même si échec)
            Payment paymentLog = new Payment();
            paymentLog.setTransactionRef(paymentId);
            paymentLog.setMethod(paymentGateway.getGatewayName());
            paymentLog.setStatus(verificationResult.getStatus());
            paymentLog.setAmount(verificationResult.getAmount() != null
                    ? new BigDecimal(verificationResult.getAmount()).divide(new BigDecimal(1000), RoundingMode.HALF_UP)
                    : subscription.getPrice());
            paymentLog.setSubscription(subscription);
            paymentRepository.save(paymentLog);

            log.info("Payment log created: paymentId={} status={} for subscription={}",
                    paymentId, verificationResult.getStatus(), subscriptionId);

            // 4. Si paiement réussi, activer la subscription (les dates sont déjà définies)
            if (verificationResult.isVerified() && "SUCCESS".equalsIgnoreCase(verificationResult.getStatus())) {
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscriptionRepository.save(subscription);

                log.info("Subscription {} activated: startDate={} endDate={}",
                        subscriptionId, subscription.getStartDate(), subscription.getEndDate());

                return new PaymentVerificationResponse(
                        true,
                        "SUCCESS",
                        "Subscription activated successfully",
                        subscriptionId
                );
            } else {
                // Paiement échoué
                subscription.setStatus(SubscriptionStatus.FAILED);
                subscriptionRepository.save(subscription);

                log.warn("Payment failed for subscription {}: status={}", subscriptionId, verificationResult.getStatus());

                return new PaymentVerificationResponse(
                        false,
                        verificationResult.getStatus(),
                        "Payment verification failed",
                        subscriptionId
                );
            }

        } catch (Exception e) {
            log.error("Error verifying payment", e);
            return new PaymentVerificationResponse(false, "ERROR", e.getMessage(), null);
        }
    }

    @Override
    public Payment findByTransactionRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef).orElse(null);
    }
}

