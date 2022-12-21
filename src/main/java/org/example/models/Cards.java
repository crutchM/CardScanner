package org.example.models;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Cards {

    private Card[] cards;


    public Cards() throws FileNotFoundException {
        Gson gson = new Gson();
        var json = this.readFile();
        this.cards =  gson.fromJson(json, Card[].class);

    }

    public void  setBalance(String cardId, int balance){
        for (int i = 0; i < this.cards.length-1; i++){
            if (this.cards[i].getCardId().equals(cardId)){
                this.cards[i].setBalance(balance);
            }
        }
    }

    public void commitChanges(){

    }



    public Card getCard(String  id){
        Card result = new Card();
        for(Card e : this.cards){
            if (e.getCardId().equals(id)){
                result = e;
            }
        }

        return result;
    }


    private String readFile() throws FileNotFoundException {
        String result = "";
        BufferedReader br = new BufferedReader(new FileReader("/home/crutchm/IdeaProjects/CardScanner/src/main/java/org/example/models/dataSource/cards.json"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null){
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            result = sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public String toString() {
        return "Cards{" +
                "cards=" + Arrays.toString(cards) +
                '}';
    }
}
