package org.example;

import com.sun.source.util.TaskEvent;
import jssc.*;
import org.example.CarWasherController.SerialPortReader;
import org.example.CardScanner.CardStatusListener;
import org.example.CardScanner.PortReader;
import org.example.models.Card;
import org.example.models.Cards;
import org.example.sys.RedisCon;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static SerialPort serialPort;

    public static void main(String[] args) throws SerialPortException, InterruptedException, IOException {
        var ports = SerialPortList.getPortNames();
        var port = ports[0];
        CheckPort(port);

        var jedis = RedisCon.pool.getResource();
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    RedisCon.PerformResponse(message);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(message);
            }
        }, "toServer");
        //initPort(port);
        while (true){

        }
    }

    public static String CheckPort(String port){
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE
            );
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            var reader = new PortReader(serialPort);
            reader.addCardStateListener(new CardStatusListener() {
                @Override
                public void cardIsAttached(String id) throws FileNotFoundException {
                    var temp = id.replace("\r\n", " ");
                    String redisMsg = "";
                    temp = temp.replace("CARD UID", "");
                    System.out.println("Приложили: " + temp);
                    if (temp.contains("READY")){
                        redisMsg = "status=READY";
                    } else redisMsg = "cardIn=" + temp;
                    try (Jedis jedis = RedisCon.pool.getResource()){

                        jedis.publish("toClient", redisMsg);

                    }

                }

                @Override
                public void cardIsRemoved(String id) {
                    var temp = id.replace("\r\n", " ");
                    temp = temp.replace("CARD UID", "");
                    System.out.println("Убрали: " + temp);
                    try (Jedis jedis = RedisCon.pool.getResource()){
                        jedis.publish("toClient", "cardOut=" + temp);
                    }
                }
            });
            serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return "null";
    }
    private static void initPort(String port){

        serialPort = new SerialPort(port);
            try {
                serialPort.openPort();
                serialPort.setParams(
                        SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE
                );
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_XONXOFF_OUT);
                var reader = new SerialPortReader(serialPort);
                reader.addCardStateListener(new CardStatusListener() {
                    @Override
                    public void cardIsAttached(String id) {
                        System.out.println(id);
                    }

                    @Override
                    public void cardIsRemoved(String id) {
                        System.out.println(id);
                    }
                });
                serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
            } catch (SerialPortException e){
            System.out.println(e);
        }
    }
}

