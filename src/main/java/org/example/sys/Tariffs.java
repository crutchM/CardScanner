package org.example.sys;

public class Tariffs {
    public static int RELAY_1 = 20;
    public static int RELAY_2 = 25;
    public static int RELAY_3 = 25;
    public static int RELAY_4 = 5;
    public static int RELAY_5 = 22;
    public static int RELAY_6 = 10;
    public static int RELAY_7 = 20;
    public static int RELAY_8 = 20;

    public static int getTarriff(String relay){
        if (relay.equals(String.format("%014x", Relay.RELAY_1 ))){
            return RELAY_1;
        }
        if (relay.equals(String.format("%014x", Relay.RELAY_2 ))){
            return RELAY_2;
        }if (relay.equals(String.format("%014x", Relay.RELAY_3 ))){
            return RELAY_3;
        }if (relay.equals(String.format("%014x", Relay.RELAY_4 ))){
            return RELAY_4;
        }if (relay.equals(String.format("%014x", Relay.RELAY_5 ))){
            return RELAY_5;
        }if (relay.equals(String.format("%014x", Relay.RELAY_6 ))){
            return RELAY_6;
        }if (relay.equals(String.format("%014x", Relay.RELAY_7 ))){
            return RELAY_7;
        }
        if (relay.equals(String.format("%014x", Relay.RELAY_8 ))){
            return RELAY_8;
        }

        return 0;
    }
}
