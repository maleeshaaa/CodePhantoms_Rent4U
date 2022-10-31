package com.example.carrentalapp_rent4u;

public class Payment {
    private String paymentId;
    private String cardNumber;
    private String cardHName;
    private String expDate;
    private String cvcCode;

    public Payment() {}

    public Payment(String paymentId, String cardNumber, String cardHName, String expDate, String cvcCode) {
        this.paymentId = paymentId;
        this.cardNumber = cardNumber;
        this.cardHName = cardHName;
        this.expDate = expDate;
        this.cvcCode = cvcCode;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHName() {
        return cardHName;
    }

    public void setCardHName(String cardHName) {
        this.cardHName = cardHName;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCvcCode() {
        return cvcCode;
    }

    public void setCvcCode(String cvcCode) {
        this.cvcCode = cvcCode;
    }
}
