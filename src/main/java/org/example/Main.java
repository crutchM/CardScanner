package org.example;

import jssc.*;

public class Main {
    private static SerialPort serialPort;

    public static void main(String[] args) {
        var ports = SerialPortList.getPortNames();
        for (String name : ports) {
            CheckPort(name);
        }

    }


    public static void CheckPort(String port){
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
            System.out.println(serialPort.readString());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
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

