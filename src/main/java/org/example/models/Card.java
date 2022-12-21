package org.example.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Card {
    private int id;
    private String cardId;
    private String cardName;
    private int balance;
    private String description;

    public Card() {
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

    public void changeBalance(int dif){
        this.balance += dif;
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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardId='" + cardId + '\'' +
                ", cardName='" + cardName + '\'' +
                ", balance=" + balance +
                ", description='" + description + '\'' +
                '}';
    }
}
