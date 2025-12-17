package com.javaquery.util.enums;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
public enum PaymentStatus {
    PAID,
    UNPAID,
    PARTIALLY_PAID,
    REFUNDED,
    CANCELLED,
    PENDING,
    AUTHORIZED,
    VOIDED,
    FAILED,
    CHARGEBACK,
    SETTLED,
    PROCESSING,
    DISPUTED,
    HOLD,
    RELEASED,
    REVERSED,
    REJECTED,
    CLEARED,
    UNCLEARED,
    OVERDUE,
    DUE,
    PAID_OUT,
    PAID_IN,
    TRANSFERRED,
    DEPOSITED,
    WITHDRAWN,
    HOLDING,
    RELEASE,
    REVERSAL,
    REJECT,
    CLEAR,
    UNCLEAR,
    OVERDUE_PAYMENT,
    DUE_PAYMENT,
    PAID_OUT_PAYMENT,
    PAID_IN_PAYMENT,
    TRANSFERRED_PAYMENT;

    public static PaymentStatus fromString(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
