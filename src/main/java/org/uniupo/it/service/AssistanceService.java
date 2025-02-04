package org.uniupo.it.service;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.uniupo.it.FaultGenerator;
import org.uniupo.it.dao.MachineDbImpl;
import org.uniupo.it.model.FaultMessage;
import org.uniupo.it.util.Topics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssistanceService {
    final private String machineId;
    final private MqttClient mqttClient;
    final private String instituteId;
    final private FaultGenerator faultGenerator;
    final private Gson gson;

    public AssistanceService(String machineId, String instituteId, MqttClient mqttClient, FaultGenerator faultGenerator) throws MqttException {
        this.machineId = machineId;
        this.mqttClient = mqttClient;
        this.instituteId = instituteId;
        this.faultGenerator = faultGenerator;
        this.gson = new Gson();
        this.mqttClient.subscribe(String.format(Topics.ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC, instituteId, machineId), this::checkMachineStatusHandler);
        this.mqttClient.subscribe(String.format(Topics.HEARTBEAT_TOPIC, instituteId, machineId), this::heartbeatHandler);
        this.mqttClient.subscribe(String.format(Topics.TECHNICIAN_ASSISTANCE_TOPIC, instituteId, machineId), this::technicianAssistanceHandler);
        this.mqttClient.subscribe(String.format(Topics.KILL_SERVICE_TOPIC, instituteId, machineId), this::killServiceHandler);

    }

    private void killServiceHandler(String topic, MqttMessage message) {
        System.out.println("Service killed hello darkness my old friend :(");
        faultGenerator.stopGenerator();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect();
                }
                mqttClient.close();
                System.exit(0);
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
                Runtime.getRuntime().halt(1);
            }
        }).start();
    }



    public void technicianAssistanceHandler(String topic, MqttMessage message) {
        try {
            System.out.println("Received technician assistance request");
            MachineDbImpl machineDb = new MachineDbImpl();

            List<FaultMessage> solvedFaults = new ArrayList<>();

            solvedFaults.addAll(machineDb.handleConsumableFaults()
                    .stream()
                    .map(fault -> new FaultMessage(
                            machineId,
                            fault.getDescription(),
                            Integer.parseInt(instituteId),
                            fault.getTimestamp(),
                            fault.getIdFault(),
                            fault.getFaultType()
                    ))
                    .toList());

            solvedFaults.addAll(machineDb.solveGenericFaults()
                    .stream()
                    .map(fault -> new FaultMessage(
                            machineId,
                            fault.getDescription(),
                            Integer.parseInt(instituteId),
                            fault.getTimestamp(),
                            fault.getIdFault(),
                            fault.getFaultType()
                    ))
                    .toList());

            if (!solvedFaults.isEmpty()) {
                List<UUID> solvedFaultsIds = solvedFaults.stream().map(FaultMessage::getIdFault).toList();
                String jsonMessage = gson.toJson(solvedFaultsIds);
                System.out.println("Sending resolved faults: " + jsonMessage);
                mqttClient.publish(String.format(Topics.MANAGEMENT_RESOLVE_FAULT_TOPIC), new MqttMessage(jsonMessage.getBytes()));
            }
        } catch (Exception e) {
            System.err.println("Error handling technician assistance: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void checkMachineStatusHandler(String topic, MqttMessage mqttMessage) {
        try {
            System.out.println("Checking machine status");
            MachineDbImpl machineDb = new MachineDbImpl();
            String jsonMessage = gson.toJson(machineDb.checkMachineStatus(), Boolean.class);
            mqttClient.publish(String.format(Topics.RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC, instituteId, machineId), new MqttMessage(jsonMessage.getBytes()));
            System.out.println("Sent machine status response on" + String.format(Topics.RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC, instituteId, machineId));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private void heartbeatHandler(String topic, MqttMessage message) {
        System.out.println("Received heartbeat from machine " + machineId);
        try {
            MqttMessage mqttMessage = new MqttMessage("ACK".getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(String.format(Topics.HEARTBEAT_RESPONSE_TOPIC, instituteId, machineId), mqttMessage);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


}
