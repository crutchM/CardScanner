package org.example;

import jssc.*;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static SerialPort serialPort;

    public static void main(String[] args) throws SerialPortException {
        var ports = SerialPortList.getPortNames();
        var port = ports[0];
        CheckPort(port);
        while(true){
        }
        //;
        //ArrayList<String> availablePorts = new ArrayList<String>();
//        for (String name : ports) {
//            String port = CheckPort(name);
//            if (port != "null"){
//                System.out.println(port);
//                availablePorts.add(port);
//            };
//        }
//        System.out.println("done");



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
            var reader = new PortReader(serialPort);
            reader.addCardStateListener(new CardStatusListener() {
                @Override
                public void cardIsAttached(String id) {
                    System.out.println("Приложили: " + id);
                }

                @Override
                public void cardIsRemoved(String id) {
                    System.out.println("Убрали: " + id);
                }
            });
            serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return "null";
    }





}
class PortReader implements SerialPortEventListener {
    private String actualCard = "";

    private CardStatusListener listener = null;
    private String lastChanges = "";
    private long timeStamp = 0;
    private SerialPort serialPort;

    private Timer timer = new Timer();

    private boolean disconnected = false;

    private  StringBuilder builder = new StringBuilder();

    public PortReader(SerialPort serialPort) {
        this.serialPort = serialPort;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(!serialPort.isCTS() && !disconnected){
                        System.out.println("связь потеряна");
                        disconnected = true;
                    }
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
                if (System.currentTimeMillis() - timeStamp >=300 && !actualCard.isEmpty()){
                    if (listener != null){
                        listener.cardIsRemoved(actualCard.split(":")[1].trim());
                    }
                    actualCard = "";
                }
            }
        }, 0, 500);

    }
    public void addCardStateListener(CardStatusListener listener){
        this.listener = listener;
    }
    @Override
    public void serialEvent(SerialPortEvent event) {
        this.disconnected = false;
        try {
        if(event.isRXCHAR() && event.getEventValue() > 0){

                //Получаем ответ от устройства, обрабатываем данные и т.д.
                String data = serialPort.readString(event.getEventValue());
                //И снова отправляем запрос
                builder.append(data);
                if (data.contains("\r\n")){
                    var tmp = builder.toString();
                    var result = tmp.replace('\t', ' ');
                    this.actualCard = result;
                    if (!this.lastChanges.equals(result.split("\r\n")[0])){
                        this.lastChanges = result.split("\r\n")[0];
                        if(listener != null){
                            this.listener.cardIsAttached(this.actualCard.split(":")[1].trim());
                        }
                    }
                    this.timeStamp = System.currentTimeMillis();
                    builder = new StringBuilder();
                }
            }
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
}

