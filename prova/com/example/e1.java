package prova.com.example;

import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class e1 {
    public static void main(String[] args) throws Exception {
        String broker = "tcp://broker.hivemq.com:1883";
        String clientID = "Client1";
        String topic = "VSC/topic";
        int qos = 2;

        MqttClient client = new MqttClient(broker, clientID);
        MqttConnectOptions connOpts = new MqttConnectOptions();

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connessione persa, motivo: " + cause);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {

                //System.out.println("Consegna completata: " + arg0.getMessageId());

            }

            @Override
            public void messageArrived(String arg0, MqttMessage message) throws Exception {
                //String msg = new String(message.getPayload());
                //System.out.println("messaggio: " + msg + "\n");
            }

        });

        client.connect(connOpts);
        client.subscribe(topic, qos);
        System.out.println("Iscritto al topic: " + topic);

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.print("Scrivi il messaggio (nome età mediaVoti): ");
                String msg = input.nextLine();
                if (!msg.trim().isEmpty()) {
                    MqttMessage userMqttMessage = new MqttMessage(msg.getBytes());
                    userMqttMessage.setQos(qos);
                    client.publish(topic, userMqttMessage);
                    System.out.println("Messaggio inviato: " + msg);
                } else {
                    System.out.println("Messaggio vuoto, non inviato.");
                }
            }
        }
        // input.close(); // Non chiudere lo scanner in un ciclo infinito
    }

}
