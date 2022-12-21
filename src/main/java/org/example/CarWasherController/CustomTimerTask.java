package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.example.models.OutputCommands;

import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {

    private SerialPortSender serialPortSender;

    private int balance;

    private final int tariff;

    private int timeLeft = 0;

    public CustomTimerTask(SerialPort serialPort, int tariff, int timeLeft){
        this.serialPortSender = new SerialPortSender(serialPort);
        this.tariff = tariff;
        this.timeLeft = timeLeft;
    }

    @Override
    public void run()  {
        try{
            this.setData();
            this.balance -= this.tariff;
            this.timeLeft -=1;
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
    }


    private void setData() throws SerialPortException {
        this.serialPortSender.sendCommand(OutputCommands.CTV, String.format("%014x", timeLeft));
        this.serialPortSender.sendCommand(OutputCommands.CBV, String.format("%014x", balance));
    }
}
