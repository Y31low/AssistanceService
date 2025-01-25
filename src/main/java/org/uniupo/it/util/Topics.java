package org.uniupo.it.util;

public class Topics {
    public static final String RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC = "macchina/%s/transaction/checkMachineStatusResponse";
    public static final String DISPENSE_COMPLETED_TOPIC = "macchina/%s/dispenser/dispenseCompleted";
    public static final String ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC = "macchina/%s/assistance/checkMachineStatus";
    public static final String HEARTBEAT_TOPIC = "macchinette/heartbeat/%s/%s/request";
    public static final String HEARTBEAT_RESPONSE_TOPIC = "macchinette/heartbeat/%s/%s/response";
}
