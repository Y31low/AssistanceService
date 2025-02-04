package org.uniupo.it.util;

public class Topics {
    public static final String HEARTBEAT_TOPIC = "macchinette/heartbeat/%s/%s/request";
    public static final String TECHNICIAN_ASSISTANCE_TOPIC = "macchinette/%s/%s/assistance/technician";
    public static final String KILL_SERVICE_TOPIC = "macchinette/%s/%s/killService";
    public static final String HEARTBEAT_RESPONSE_TOPIC = "macchinette/heartbeat/%s/%s/response";
    public static final String GENERIC_FAULT_TOPIC = "management/faults/newFault";
    public static final String MANAGEMENT_RESOLVE_FAULT_TOPIC = "management/faults/resolve";

    private final static String BASE_TOPIC = "istituto/%s/macchina/%s";
    public static final String RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC = BASE_TOPIC + "/transaction/checkMachineStatusResponse";
    public static final String ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC = BASE_TOPIC + "/assistance/checkMachineStatus";


}
