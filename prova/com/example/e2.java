package prova.com.example;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class Studente{
    String nome;
    int eta;
    float mediaVoti;

    // Costruttore della classe Studente
    public Studente(String nome, int eta, float mediaVoti) {
        this.nome = nome;
        this.eta = eta;
        this.mediaVoti = mediaVoti;
    }
};

public class e2 
{
    public static void main(String[] args) throws Exception 
    {
        String broker = "tcp://broker.hivemq.com:1883";
        String clientID = "Client2"; // Cambia clientID per evitare conflitti
        String topic = "VSC/topic";
        int qos = 2;
        MqttClient client = new MqttClient(broker, clientID);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        ArrayList<Studente> stud = new ArrayList<>();
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connessione persa, motivo: " + cause);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
                System.out.println("Consegna completata: " + arg0.getMessageId());
            }
            @Override
            public void messageArrived(String arg0, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                // Estrae il nome dalla stringa del messaggio ricevuto, separando per spazio
                String[] parts = msg.trim().split(" ");
                if (parts.length != 3) {
                    return;
                }
                try {
                    String nome = parts[0].trim();
                    int eta = Integer.parseInt(parts[1].trim());
                    float mediaVoti = Float.parseFloat(parts[2].trim());
                    Studente studTemp = new Studente(nome, eta, mediaVoti);
                    stud.add(studTemp);
                    
                    
                } catch (Exception ex) {
                    System.out.println("Errore nel parsing del messaggio ricevuto: " + ex.getMessage());
                }
            }
        });
        client.connect(connOpts);
        client.subscribe(topic, qos);
        System.out.println("Iscritto al topic: " + topic);
        try (java.util.Scanner input = new java.util.Scanner(System.in)) {
            while (true) {
                
                printMenu();
                String scelta = input.nextLine();
                switch (scelta) {
                    
                    case "1":
                        clearScreen();
                        System.out.println("--- Studenti ---");
                        if (stud.isEmpty()) {
                            System.out.println("Nessuno studente registrato.");
                        } else {
                            for (Studente s : stud) {
                                System.out.println("Nome: " + s.nome + ", Età: " + s.eta + ", Media Voti: " + s.mediaVoti);
                            }
                        }
                        System.out.println("-----------------");
                        break;
                    case "2":
                        clearScreen();
                        System.out.print("Scrivi il messaggio (nome età mediaVoti): ");
                        String msg = input.nextLine();
                        if (!msg.trim().isEmpty()) {
                            MqttMessage userMqttMessage = new MqttMessage(msg.getBytes());
                            userMqttMessage.setQos(qos);
                            client.publish(topic, userMqttMessage);
                            //System.out.println("Messaggio inviato: " + msg);
                        } else {
                            System.out.println("Messaggio vuoto, non inviato.");
                        }
                        break;
                    case "0":
                        return; // Esci dal main
                    default:
                        clearScreen();
                        System.out.println("Scelta non valida, riprova.");
                }
            }
        } 
    }

    public static void printMenu() {
        
        System.out.println("Menu:");
        System.out.println("1. Visualizza studenti");
        System.out.println("2. Invia un messaggio");
        System.out.println("0. Esci");
        System.out.print("> ");

    }

    public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
    }

}

