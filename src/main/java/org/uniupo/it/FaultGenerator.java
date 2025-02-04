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
    private String instituteId;
    private final Gson gson = new Gson();


    public FaultGenerator(String machineId,String instituteId, MqttClient mqttClient) {
        this.machineId = machineId;
        this.random = new Random();
        this.running = true;
        this.instituteId = instituteId;
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

                    try {
                        List<FaultMessage> faults = List.of(fault);
                        MqttMessage message = new MqttMessage(gson.toJson(faults).getBytes());
                        message.setQos(1);
                        mqttClient.publish(Topics.GENERIC_FAULT_TOPIC, message);
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
        UUID faultId = UUID.randomUUID();
        Fault fault = new Fault(description, faultId, timestamp, FaultType.GUASTO_GENERICO);

        MachineDb machineDb = new MachineDbImpl(instituteId,machineId);
        machineDb.insertFaults(List.of(fault));
        System.out.println("Fault generated: " + fault);
        machineDb.setMachineStatus(true);

        return new FaultMessage(machineId, description, Integer.parseInt(instituteId), timestamp, faultId, FaultType.GUASTO_GENERICO);
    }

    public void stopGenerator() {
        running = false;
        interrupt();
    }
}