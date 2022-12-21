package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.example.models.Card;
import org.example.models.Cards;
import org.example.sys.OutputCommands;
import org.example.sys.Tariffs;

import java.io.FileNotFoundException;
import java.util.Timer;

public class ButtonsHandler {

    private SerialPort serialPort;

    private Timer timer = new Timer();

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    private String cardId;

    private SerialPortSender serialPortSender;

    public boolean cardsIsAttached=false;



    public ButtonsHandler(SerialPortSender serialPortSender, SerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPortSender = new SerialPortSender(serialPort);
    }


    public void SwitchRelayStatus(String relay) throws SerialPortException, InterruptedException, FileNotFoundException {
        if (!this.cardsIsAttached){
            return;
        }
        Cards cards = new Cards();
        cardId = cardId.replace("000000", "");
        Card card = cards.getCard(cardId.toUpperCase());
        if (card.getBalance() == 0){
            return;
        }
        var tariff = Tariffs.getTarriff(relay);
        var statuses = this.serialPort.readString();
        Thread.sleep(100);
        if (statuses == null || statuses.equals("RES00000000000000")  ){
            this.serialPortSender.sendCommand(OutputCommands.TRE, relay);
        } else {
            this.serialPortSender.sendCommand(OutputCommands.TRD, relay);
//            try{
//                this.timer.cancel();
//            } catch (IllegalStateException e){
//                return;
//            }
        }
        final int[] time = {card.getBalance() / tariff};
        this.sendStatus(card, tariff);
        CustomTimerTask timerTask = new CustomTimerTask(serialPort, tariff, time[0], card, cards);
        try {
            this.timer.scheduleAtFixedRate(timerTask, 0, 3000);
        } catch (IllegalStateException e){
            System.out.println("таймер остановлен");
        }
        Thread.sleep(cards.getCard(cardId).getBalance()/tariff * 3000L);


    }

    private void  sendStatus(Card card, int tarriff) throws SerialPortException {
        String sendingTime = String.format("%014x", card.getBalance()/tarriff);
        String sendingBalance = String.format("%014x", card.getBalance());
        this.serialPortSender.sendCommand(OutputCommands.CTV, deleteZero(sendingTime));
        this.serialPortSender.sendCommand(OutputCommands.CBV, deleteZero(sendingBalance));
    }

    private String deleteZero(String line){
        int i = line.length() - 1;
        var tmp = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        while (sb.length() <= 14 && i >= 0){
            sb.append(tmp[i]);
            i--;
        }

        return sb.reverse().toString();
    }

    public void dropTimer(){
        this.timer.cancel();
    }

    private void GetRelayStatuses() throws SerialPortException {
        this.serialPortSender.sendCommand(OutputCommands.GRS, "");
    }
}
