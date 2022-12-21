package org.example.CardScanner;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

public class PortReader implements SerialPortEventListener {


    private String actualCard = "";

    private CardStatusListener listener = null;
    private String lastChanges = "";
    private long timeStamp = 0;
    private SerialPort serialPort;

    private Timer timer = new Timer();

    private boolean disconnected = false;

    private StringBuilder builder = new StringBuilder();

    public PortReader(SerialPort serialPort) {
        this.serialPort = serialPort;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!serialPort.isCTS() && !disconnected) {
                        System.out.println("связь потеряна");
                        disconnected = true;
                    }
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
                if (System.currentTimeMillis() - timeStamp >= 300 && !actualCard.isEmpty()) {
                    if (listener != null) {
                        listener.cardIsRemoved(actualCard.split(":")[1].trim());
                        lastChanges = "";
                    }
                    actualCard = "";
                }
            }
        }, 0, 500);

    }

    public void addCardStateListener(CardStatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        this.disconnected = false;
        try {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                String data = serialPort.readString(event.getEventValue());
                builder.append(data);
                if (data.contains("\r\n")) {
                    var tmp = builder.toString();
                    var result = tmp.replace('\t', ' ');
                    this.actualCard = result;
                    if (!this.lastChanges.equals(result.split("\r\n")[0])) {
                        this.lastChanges = result.split("\r\n")[0];
                        if (listener != null) {
                            this.listener.cardIsAttached(this.actualCard.split(":")[1].trim());
                        }
                    }
                    this.timeStamp = System.currentTimeMillis();
                    builder = new StringBuilder();
                }
            }
        } catch (SerialPortException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
