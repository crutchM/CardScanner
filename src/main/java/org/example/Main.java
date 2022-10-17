package org.example;

import jssc.*;

import java.util.ArrayList;

public class Main {
    private static SerialPort serialPort;

    public static void main(String[] args) {
        var ports = SerialPortList.getPortNames();
        ArrayList<String> availablePorts = new ArrayList<String>();
        for (String name : ports) {
            String port = CheckPort(name);
            if (port != "null"){
                System.out.println(port);
                availablePorts.add(port);
            };
        }
        System.out.println("done");



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
            //serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            serialPort.writeString("WHOAREYOU");
            return serialPort.readString();
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return "null";
    }




    private static class PortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    String data = serialPort.readString(event.getEventValue());
                    //И снова отправляем запрос
                    serialPort.writeString("WHOAREYOU");
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}

