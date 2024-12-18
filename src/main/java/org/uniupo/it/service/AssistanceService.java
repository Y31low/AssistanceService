package org.uniupo.it.service;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
        this.mqttClient.subscribe(baseTopic + "/request", this::requestHandler);
    }

    private void requestHandler(String s, MqttMessage mqttMessage) {
    }
}
