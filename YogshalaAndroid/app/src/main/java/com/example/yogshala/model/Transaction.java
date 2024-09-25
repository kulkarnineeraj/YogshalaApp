package com.example.yogshala.model;

public class Transaction {
    private String transactionId;
    private String clientId;

    private String clientName;
    private String fromDate;

    private String toDate;
    private String type;
    private String monthFee;
    private String program;

    private String receivedAmount;
    private String remarks;

    private String paymentMode;

    public Transaction() {
    }

    // Constructor
    public Transaction(String transactionId, String clientId, String clientName, String fromDate, String toDate, String type, String monthFee, String receivedAmount, String program, String remarks, String paymentMode) {
        this.transactionId = transactionId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.type = type;
        this.monthFee = monthFee;

        this.receivedAmount = receivedAmount;
        this.program = program;
        this.remarks = remarks;
        this.paymentMode = paymentMode;
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMonthFee() {
        return monthFee;
    }

    public void setMonthFee(String monthFee) {
        this.monthFee = monthFee;
    }

    public String getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(String receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }


}
