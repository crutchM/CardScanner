package org.example.models;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
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

    public void commitChanges() throws IOException {
        Gson gson = new Gson();
        var res = gson.toJson(this);
        res = res.replace("{\"cards\":", "");
        res = res.replace("]}", "]");
        File file = new File("/home/crutchm/IdeaProjects/CardScanner/src/main/java/org/example/models/dataSource/cards.json");
        FileOutputStream fo = new FileOutputStream(file, false);
        byte[] bytes = res.getBytes();
        fo.write(bytes);
        fo.close();
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
