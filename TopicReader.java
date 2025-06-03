package com.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TopicReader {
    public static void main(String[] args) {
        String broker = "tcp://broker.hivemq.com:1883";
        String clientID = "client2";
        String topic = "chat/topic";
        int qos = 2;
        try {
            MqttClient client = new MqttClient(broker, clientID);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setConnectionTimeout(0);
            connOpts.setAutomaticReconnect(true);
            connOpts.setKeepAliveInterval(10);
            connOpts.setCleanSession(false);
            connOpts.setMaxInflight(100);
            //connOpts.setWill(topic, (clientID + " disconnected").getBytes(), qos, false);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connessione persa: " + cause);
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String msg = new String(message.getPayload());
                    String[] part = estraiClientEMessaggio(msg);
                    String clientId = part[0];
                    if(!clientId.equals(clientID)) {
                        System.out.println("Ricevuto messaggio da " + clientId + " sul topic " + topic + ": " + part[1]);
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
            client.connect(connOpts);
            System.out.println("Connesso al broker: " + broker);
            client.subscribe(topic, qos);
            System.out.println("Sottoscritto al topic: " + topic);
            // Attende e mostra i messaggi ricevuti
            final Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // Thread interrotto, termina il programma
                }
            }
        } catch (MqttException e) {
            System.out.println("Errore MQTT: " + e.getMessage());
        }
    }
    public static String[] estraiClientEMessaggio(String testo) {
        if (testo == null || testo.trim().isEmpty()) {
            return new String[]{"", ""};
        }
        int primoSpazio = testo.indexOf(' ');
        if (primoSpazio == -1) {
            return new String[]{testo, ""};
        }
        String clientId = testo.substring(0, primoSpazio);
        String messaggio = testo.substring(primoSpazio + 1);
        return new String[]{clientId, messaggio};
    }
}