package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.example.models.Card;
import org.example.models.Cards;
import org.example.sys.OutputCommands;

import java.io.IOException;
import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {

    private SerialPortSender serialPortSender;

    public Card card;

    public Cards cards;

    private final int tariff;

    public int timeLeft = 0;

    public CustomTimerTask(SerialPort serialPort, int tariff, int timeLeft, Card card, Cards cards){
        this.cards = cards;
        this.card = card;
        this.serialPortSender = new SerialPortSender(serialPort);
        this.tariff = tariff;
        this.timeLeft = timeLeft;
    }

    @Override
    public void run()  {
        try{
            this.setData();
            var dif = this.card.getBalance() - this.tariff;
            this.cards.setBalance(this.card.getCardId(), dif);
            this.card.setBalance(dif);
            this.timeLeft -= 1;
            this.cards.commitChanges();
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void setData() throws SerialPortException {
        if (timeLeft <0){
            timeLeft = 0;
            this.serialPortSender.sendCommand(OutputCommands.TRD, "000000000000FF");
            System.out.println("Реле закрыты");
            //this.cancel();
        }

        if (this.card.getBalance() < 0){
            this.card.setBalance(0);
            this.cards.setBalance(this.card.getCardId(), 0);
            this.serialPortSender.sendCommand(OutputCommands.TRD, "000000000000FF");
            System.out.println("Реле закрыты");
            //this.cancel();
        }
        this.serialPortSender.sendCommand(OutputCommands.CTV, String.format("%014x", timeLeft));
        this.serialPortSender.sendCommand(OutputCommands.CBV, String.format("%014x", this.card.getBalance()));
    }
}
