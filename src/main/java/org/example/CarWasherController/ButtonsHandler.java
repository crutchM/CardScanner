package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.example.models.Card;
import org.example.models.Cards;
import org.example.models.OutputCommands;
import org.example.models.Tariffs;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

public class ButtonsHandler {

    private SerialPort serialPort;

    private Timer timer = new Timer();

    private SerialPortSender serialPortSender;

    public boolean cardsIsAttached=false;



    public ButtonsHandler(SerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPortSender = new SerialPortSender(serialPort);
    }


    public void SwitchRelayStatus(String relay, String cardId) throws SerialPortException, InterruptedException, FileNotFoundException {
        if (!this.cardsIsAttached){
            return;
        }
        Cards cards = new Cards();
        Card card = cards.getCard(cardId);
        if (card.getBalance() == 0){
            return;
        }
        var tariff = Tariffs.getTarriff(relay);
        var statuses = this.serialPort.readString();
        if (statuses == null || statuses.equals("RES00000000000000")  ){
            this.serialPortSender.sendCommand(OutputCommands.TRE, relay);
        } else {
            this.serialPortSender.sendCommand(OutputCommands.TRD, relay);
            this.timer.cancel();
        }
        final int[] time = {card.getBalance() / tariff};
        this.sendStatus(card, tariff);
        CustomTimerTask task = new CustomTimerTask(this.serialPort, tariff, card.getBalance()/tariff);
        //this.timer.scheduleAtFixedRate(task, 0, 1000);
        this.GetRelayStatuses();
        Thread.sleep(100);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendStatus(card, tariff);
                    time[0] -= 1;
                    cards.setBalance(card.getCardId(),-tariff);
                    cards.commitChanges();
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
            }
        },0, 1000);
        Thread.sleep(cards.getCard(cardId).getBalance()/tariff * 1000L);
        this.serialPortSender.sendCommand(OutputCommands.TRD, "000000000000FF");
    }

    private void  sendStatus(Card card, int tarriff) throws SerialPortException {
        this.serialPortSender.sendCommand(OutputCommands.CTV, String.format("%014x", card.getBalance()/tarriff));
        this.serialPortSender.sendCommand(OutputCommands.CBV, String.format("%014x", card.getBalance()));
    }

    private void setupTimer(int balance, int tariff){

    }

    private void GetRelayStatuses() throws SerialPortException {
        this.serialPortSender.sendCommand(OutputCommands.GRS, "");
    }
}
