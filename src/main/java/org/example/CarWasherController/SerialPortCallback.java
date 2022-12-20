package org.example.CarWasherController;

public class SerialPortCallback {

    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content.length() > 17){
            System.out.println(content);
        } else {
            this.content = content;
        }
    }



    public SerialPortCallback(String content) {
        this.setContent(content);
    }
}
