package org.example.models;


public class Operation {

    private int id;
    private String cardId;
    private String cardName;
    private int balance;
    private String description;

    public Operation(int id, String cardId, String cardName, int balance, String description) {
        this.id = id;
        this.cardId = cardId;
        this.cardName = cardName;
        this.balance = balance;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getCardId() {
        return cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public int getBalance() {
        return balance;
    }

    public String getDescription() {
        return description;
    }




}
