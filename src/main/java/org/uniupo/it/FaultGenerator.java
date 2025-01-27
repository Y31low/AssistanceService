package org.uniupo.it;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.uniupo.it.dao.MachineDb;
import org.uniupo.it.dao.MachineDbImpl;
import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultMessage;
import org.uniupo.it.model.FaultType;
import org.uniupo.it.util.Topics;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FaultGenerator extends Thread {
    private final String machineId;
    private final Random random;
    private final List<String> genericFaultDescriptions;
    private volatile boolean running;
    private MqttClient mqttClient;
    private final Gson gson = new Gson();


    public FaultGenerator(String machineId, MqttClient mqttClient) {
        this.machineId = machineId;
        this.random = new Random();
        this.running = true;
        this.genericFaultDescriptions = List.of(
                "Rilevato surriscaldamento del motore",
                "Malfunzionamento della pompa dell'acqua",
                "Guasto del sistema di pressione",
                "Errore del sensore di temperatura",
                "Errore di comunicazione della scheda di controllo",
                "Problema di riscaldamento della caldaia",
                "Malfunzionamento del sensore del contenitore dei rifiuti",
                "Meccanismo di macinazione bloccato",
                "Malfunzionamento dell'unità di visualizzazione"

        );
        this.mqttClient = mqttClient;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (random.nextDouble() < 0.10) {
                    FaultMessage fault = generateFault();
                    String faultJson = gson.toJson(fault);
                    try {
                        mqttClient.publish(Topics.GENERIC_FAULT_TOPIC, new MqttMessage(faultJson.getBytes()));
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
                Thread.sleep(random.nextInt(300000) + 300000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private FaultMessage generateFault() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String description = genericFaultDescriptions.get(random.nextInt(genericFaultDescriptions.size()));
        Fault fault = new Fault(description, UUID.randomUUID(), timestamp, FaultType.GUASTO_GENERICO);

        MachineDb machineDb = new MachineDbImpl();
        machineDb.insertFaults(List.of(fault));
        machineDb.setMachineStatus(true);

        return new FaultMessage(machineId, description, 111, timestamp, UUID.randomUUID(), FaultType.GUASTO_GENERICO);
    }

    public void stopGenerator() {
        running = false;
        interrupt();
    }
}