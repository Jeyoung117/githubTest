package com.example.loginactivity;

public class BankAccount {

    String userId;
    String bank;
    String accountNumber;
    int amounts;
    int digitalCheck;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAmounts() {
        return amounts;
    }

    public void setAmounts(int amounts) {
        this.amounts = amounts;
    }

    public int getDigitalCheck() {
        return digitalCheck;
    }

    public void setDigitalCheck(int digitalCheck) {
        this.digitalCheck = digitalCheck;
    }
}
