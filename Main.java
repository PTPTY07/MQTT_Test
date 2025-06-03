package com.example;

import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Main {
    public static void main(String[] args) {
        // Scanner per input da tastiera
        Scanner scanner = new Scanner(System.in);
        // Parametri di connessione MQTT
        String broker = "tcp://broker.hivemq.com:1883";
        String clientID = "client1"; // Identificativo di questo client
        String topic = "chat/topic"; // Topic condiviso per la chat
        int qos = 2; // Qualità del servizio massima

        try {
            // Creazione del client MQTT
            MqttClient client = new MqttClient(broker, clientID);
            
            // Configurazione delle opzioni di connessione
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setConnectionTimeout(0); // 0 = nessun timeout di connessione
            connOpts.setAutomaticReconnect(true);
            connOpts.setKeepAliveInterval(10); // keep-alive molto frequente
            connOpts.setCleanSession(false);
            connOpts.setMaxInflight(100);
            connOpts.setWill(topic, (clientID + " disconnected").getBytes(), qos, false); // Last Will per notificare disconnessione


            // Connessione al broker MQTT
            client.connect(connOpts);
            System.out.println("Connesso al broker: " + broker);

            // Sottoscrizione al topic della chat
            client.subscribe(topic, qos);
            System.out.println("Sottoscritto al topic: " + topic);

            // Invio messaggi
            
            while (true) {
                System.out.print("Inserisci ID: ");
                String ID = scanner.nextLine();
                System.out.print("Inserisci messaggio: ");
                String input = scanner.nextLine();
                if (!input.trim().isEmpty()) 
                {
                    MqttMessage message = new MqttMessage((ID + " " + input).getBytes());
                    message.setQos(qos);
                    client.publish(topic, message);
                    System.out.println("Messaggio inviato su " + topic + ": " + input);
                }
            }
        } catch (MqttException e) {
            System.out.println("Errore MQTT: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

