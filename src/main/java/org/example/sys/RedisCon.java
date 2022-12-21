package org.example.sys;


import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCon {
    public static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 10888);
}
