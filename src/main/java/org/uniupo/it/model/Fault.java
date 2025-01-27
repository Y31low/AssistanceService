package org.uniupo.it.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Fault {
    String description;
    UUID idFault;
    Timestamp timestamp;
    FaultType faultType;
    Boolean risolto;

    public Fault(String description, UUID idFault, Timestamp timestamp, FaultType faultType) {
        this.description = description;
        this.idFault = idFault;
        this.timestamp = timestamp;
        this.faultType = faultType;
        this.risolto = false;
    }

    public Fault(String description, UUID idFault, Timestamp timestamp, FaultType faultType, Boolean risolto) {
        this.description = description;
        this.idFault = idFault;
        this.timestamp = timestamp;
        this.faultType = faultType;
        this.risolto = risolto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getIdFault() {
        return idFault;
    }

    public void setIdFault(UUID idFault) {
        this.idFault = idFault;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public FaultType getFaultType() {
        return faultType;
    }

    public void setFaultType(FaultType faultType) {
        this.faultType = faultType;
    }

    public Boolean getRisolto() {
        return risolto;
    }

    public void setRisolto(Boolean risolto) {
        this.risolto = risolto;
    }

    @Override
    public String toString() {
        return "Fault{" +
                "description='" + description + '\'' +
                ", idFault=" + idFault +
                ", timestamp=" + timestamp +
                ", faultType=" + faultType +
                ", risolto=" + risolto +
                '}';
    }
}
