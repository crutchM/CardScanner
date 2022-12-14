# Card Scanner
## Actions before usage


we need to configure connection by serial port, and add listeners for attach and remove card

```java
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
                    var temp = id.replace("\r\n", " ");
                    temp = temp.replace("CARD UID", "");
                    System.out.println("Приложили: " + temp);
                }

                @Override
                public void cardIsRemoved(String id) {
                    var temp = id.replace("\r\n", " ");
                    temp = temp.replace("CARD UID", "");
                    System.out.println("Убрали: " + temp);
                }
            });
            serialPort.addEventListener(reader, SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return "null";
    }
```
## Connection parameters

### Arduino
```java
serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE
            );
```

### STM32


```java
 serialPort.setParams(
                        SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE
                );
```


# sending commands for relay


```java
public class Relay {
    public static final int RELAY_1 = 0x1;
    public static final int RELAY_2 = 0x2;
    public static final int RELAY_3 = 0x4;
    public static final int RELAY_4 = 0x8;
    public static final int RELAY_5 = 0x10;
    public static final int RELAY_6 = 0x20;
    public static final int RELAY_7 = 0x40;
    public static final int RELAY_8 = 0x80;

}
class Program {
    public static void main() {
        var formated = String.format("Relay code: TRE%014x", Relay.RELAY_5 | Relay.RELAY_8);
    }
}
```