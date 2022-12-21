package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.example.CardScanner.CardStatusListener;
import org.example.models.Card;
import org.example.models.Cards;
import org.example.sys.OutputCommands;

import java.io.FileNotFoundException;
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

    private SerialPortSender serialPortSender;

    public void addCardStateListener(CardStatusListener listener) {
        this.listener = listener;
    }


    public SerialPortReader(SerialPort serialPort){
        this.serialPort = serialPort;
        this.serialPortSender = new SerialPortSender(serialPort);
        this.handler = new ButtonsHandler(serialPortSender, serialPort);
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

    private void performCallbacks() throws SerialPortException, InterruptedException, FileNotFoundException {
        String command = this.callback.getContent();
        if(command == null) {
            System.out.println("Ууу, ничего не пришло");
            return;
        }
        Thread.sleep(1000);
        if (command.contains("ABP")) {
            this.handler.SwitchRelayStatus(command.replace("ABP", ""));

        }
        if (command.contains("NCP")){
            this.handler.cardsIsAttached = true;
            this.handler.setCardId(command.replace("NCP", ""));
            Card card = new Cards().getCard(command.replace("NCP000000", "").toUpperCase());
            this.serialPortSender.sendCommand(OutputCommands.CTV, "00000000000000");
            this.serialPortSender.sendCommand(OutputCommands.CBV, String.format("%014x", card.getBalance()));
        }
        if (command.contains("WCL")){
            this.handler.cardsIsAttached = false;
            this.handler.setCardId("");
            this.serialPortSender.sendCommand(OutputCommands.TRD, "000000000000FF");
            this.serialPortSender.sendCommand(OutputCommands.CBV, "00000000000000");
            this.handler.dropTimer();
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
                Thread.sleep(100);

            }
        } catch (SerialPortException | InterruptedException e){
            System.out.println(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
