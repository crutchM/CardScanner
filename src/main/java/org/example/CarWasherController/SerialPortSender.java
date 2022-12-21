package org.example.CarWasherController;

import jssc.SerialPort;
import jssc.SerialPortException;
import org.example.sys.OutputCommands;

public class SerialPortSender {

    public String getCommand() {
        return command;
    }

    private String command;


    private SerialPort serialPort;

    public SerialPortSender(SerialPort serialPort){
        this.serialPort = serialPort;
    }

    public void sendCommand(OutputCommands command, String value) throws SerialPortException {
        if (value == " " || value.length() == 0){
            System.out.println("некорректное значение");
        }
        this.mapCommand(command, value);
        try {
            this.serialPort.writeString("@");
            Thread.sleep(100);
            this.serialPort.writeString(this.command);

        } catch (SerialPortException e){
            System.out.println(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    private void mapCommand(OutputCommands command, String value){
        switch (command){
            case CBV:
                    this.command = "CBV" + value;
                break;

            case CTV:
                    this.command = "CTV" + value;
                break;

            case GCI:
                    this.command = "GCI00000000000000";
                break;

            case GRS:
                    this.command = "GRS00000000000000";
                break;

            case GYN:
                    this.command = "GYN00000000000000";
                break;

            case SRS:
                    this.command = "SRS" + value;
                break;

            case TRD:
                    this.command = "TRD" + value;
                break;

            case TRE:
                    this.command = "TRE" + value;
                break;

        }
    }

}
