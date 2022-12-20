package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortException;

public class ButtonsHandler {

    private SerialPort serialPort;

    private SerialPortSender serialPortSender;



    public ButtonsHandler(SerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPortSender = new SerialPortSender(serialPort);
    }


    public String SwitchRelayStatus(String relay) throws SerialPortException {
        this.GetRelayStatuses();
        if (this.serialPort.readString().replace("RES", "").equals("00000000000000")){
            this.serialPortSender.sendCommand(OutputCommands.TRE, relay);
        } else {
            this.serialPortSender.sendCommand(OutputCommands.TRD, "000000000000FF");
        }
        String status = this.serialPort.readString();
        return status;
    }

    private void GetRelayStatuses() throws SerialPortException {
        this.serialPortSender.sendCommand(OutputCommands.GRS, "");
    }
}
