package org.uniupo.it.service;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.uniupo.it.dao.MachineDbImpl;
import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultMessage;
import org.uniupo.it.model.FaultType;
import org.uniupo.it.util.Topics;

import java.util.List;

public class AssistanceService {
    final private String machineId;
    final private MqttClient mqttClient;
    final private String instituteId;
    final private String baseTopic;
    final private Gson gson;

    public AssistanceService(String machineId, String instituteId, MqttClient mqttClient) throws MqttException {
        this.machineId = machineId;
        this.mqttClient = mqttClient;
        this.instituteId = instituteId;
        this.baseTopic = "macchina/" + machineId + "/assistance";
        this.gson = new Gson();
        this.mqttClient.subscribe(String.format(Topics.ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC, machineId), this::checkMachineStatusHandler);
        this.mqttClient.subscribe(String.format(Topics.HEARTBEAT_TOPIC, instituteId, machineId), this::heartbeatHandler);
        this.mqttClient.subscribe(String.format(Topics.TECHNICIAN_ASSISTANCE_TOPIC, instituteId, machineId), this::technicianAssistanceHandler);

    }

    private void technicianAssistanceHandler(String topic, MqttMessage message) {
        System.out.println("Received technician assistance request");
        MachineDbImpl machineDb = new MachineDbImpl();
        machineDb.solveGenericFaults();
    }


    private void checkMachineStatusHandler(String topic, MqttMessage mqttMessage) {
        try {
            System.out.println("Checking machine status");
            MachineDbImpl machineDb = new MachineDbImpl();
            String jsonMessage = gson.toJson(machineDb.checkMachineStatus(), Boolean.class);
            System.out.println(jsonMessage);
            mqttClient.publish(String.format(Topics.RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC, machineId), new MqttMessage(jsonMessage.getBytes()));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private void heartbeatHandler(String topic, MqttMessage message) {
        System.out.println("Received heartbeat from machine " + machineId);
        try {
            mqttClient.publish(String.format(Topics.HEARTBEAT_RESPONSE_TOPIC, instituteId, machineId), new MqttMessage("ACK".getBytes()));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


}
