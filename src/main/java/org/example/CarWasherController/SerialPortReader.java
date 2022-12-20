package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.example.CardScanner.CardStatusListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SerialPortReader implements SerialPortEventListener {

    private ArrayList<String> availableMessages = new ArrayList<>();

    private long timeStamp = 0;

    private ButtonsHandler handler;
    private SerialPortCallback callback;


    private boolean disconnected = false;

    private Timer timer = new Timer();

    private CardStatusListener listener = null;

    private SerialPort serialPort;

    public void addCardStateListener(CardStatusListener listener) {
        this.listener = listener;
    }


    public SerialPortReader(SerialPort serialPort){
        this.serialPort = serialPort;
        this.handler = new ButtonsHandler(serialPort);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!serialPort.isCTS() && !disconnected){
                        System.out.println("связь потеряна");
                        disconnected = true;
                    }
                } catch (SerialPortException e){
                    throw new RuntimeException(e);
                }

//                if (disconnected){
//                    listener.cardIsRemoved("stm32 was removed");
//                }
            }
        },0, 500);
    }

    private void performCallbacks() throws SerialPortException {
        var command = this.callback.getContent();
        if (command.contains("ABP")) {
            System.out.println(this.handler.SwitchRelayStatus(command.replace("ABP", "")));
        }
        if (command.contains("NCP")){

        }
        if (command.contains("WCL")){

        }
    }



    @Override
    public void serialEvent(SerialPortEvent event) {
        this.disconnected = false;
        try{
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                String response = serialPort.readString(event.getEventValue());
                if (response.length() == 0){
                    System.out.println("ничего не пришло");
                    return;
                } else if(response.equals("00000000000000000")){
                    System.out.println("системная ошибка, проверьте контроллер");
                    return;
                }
                this.callback = new SerialPortCallback(response);
                System.out.println("системное сообщение: " + response);
                this.performCallbacks();

            }
        } catch (SerialPortException e){
            System.out.println(e);
        }
    }
}
