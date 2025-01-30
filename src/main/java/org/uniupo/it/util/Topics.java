package org.uniupo.it.util;

public class Topics {
    private final static String BASE_TOPIC = "istituto/%s/macchina/%s";
    public static final String RESPONSE_ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC = BASE_TOPIC+"/transaction/checkMachineStatusResponse";
    public static final String ASSISTANCE_CHECK_MACHINE_STATUS_TOPIC = BASE_TOPIC+"/assistance/checkMachineStatus";

    public static final String HEARTBEAT_TOPIC = "macchinette/heartbeat/%s/%s/request";
    public static final String TECHNICIAN_ASSISTANCE_TOPIC = "macchinette/%s/%s/assistance/technician";
    public static final String HEARTBEAT_RESPONSE_TOPIC = "macchinette/heartbeat/%s/%s/response";
    public static final String GENERIC_FAULT_TOPIC = "management/faults/genericFault";

    public static final String SOLVED_GENERIC_FAULT_TOPIC = "management/faults/solvedGenericFault";

}
