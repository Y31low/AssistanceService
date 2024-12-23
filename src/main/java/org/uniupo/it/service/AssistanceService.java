package org.uniupo.it.service;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.uniupo.it.dao.MachineDbImpl;
import org.uniupo.it.util.Topics;

public class AssistanceService {
    final private String machineId;
    final private MqttClient mqttClient;
    final private String baseTopic;
    final private Gson gson;

    public AssistanceService(String machineId, MqttClient mqttClient) throws MqttException {
        this.machineId = machineId;
        this.mqttClient = mqttClient;
        this.baseTopic = "macchina/" + machineId + "/assistance";
        this.gson = new Gson();
        this.mqttClient.subscribe(String.format(Topics.ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC,machineId), this::checkMachineStatusHandler);

    }

    private void checkMachineStatusHandler(String topic, MqttMessage mqttMessage) {
        try {
            System.out.println("Checking machine status");
            MachineDbImpl machineDb = new MachineDbImpl();
            String jsonMessage = gson.toJson(machineDb.checkMachineStatus(), Boolean.class);
            System.out.println(jsonMessage);
            mqttClient.publish(
                    String.format(Topics.RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC, machineId),
                    new MqttMessage(jsonMessage.getBytes())
            );
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


}
