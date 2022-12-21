package org.example.sys;


import org.example.models.Cards;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileNotFoundException;
import java.io.IOException;

public class RedisCon {
    public static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 10888);

    public static void PerformResponse(String message) throws IOException {
        if (message.contains("bc")){
            int balance = Integer.parseInt(message.split("&")[0].split("=")[1]);
            String cardId = message.split("&")[1].split("=")[1].replace(" ", "");
            Cards cards = new Cards();
            cards.setBalance(cardId, balance);
            cards.commitChanges();

        }
    }
}
