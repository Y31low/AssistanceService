package org.uniupo.it.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Fault {
    private String machineId;
    private int instituteId;
    private String description;
    private Timestamp timestamp;
    private UUID idFault;
    private FaultType faultType;

    public Fault(String machineId, String description,int instituteId, Timestamp timestamp, UUID idFault, FaultType faultType) {
        this.machineId = machineId;
        this.description = description;
        this.instituteId = instituteId;
        this.timestamp = timestamp;
        this.idFault = idFault;
        this.faultType = faultType;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getIdFault() {
        return idFault;
    }

    public void setIdFault(UUID idFault) {
        this.idFault = idFault;
    }

    public FaultType getFaultType() {
        return faultType;
    }

    public void setFaultType(FaultType faultType) {
        this.faultType = faultType;
    }

    public int getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(int instituteId) {
        this.instituteId = instituteId;
    }

    @Override
    public String toString() {
        return "Fault{" +
                "machineId='" + machineId + '\'' +
                ", instituteId='" + instituteId + '\'' +
                ", description='" + description + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", idFault='" + idFault + '\'' +
                ", faultType=" + faultType +
                '}';
    }
}


