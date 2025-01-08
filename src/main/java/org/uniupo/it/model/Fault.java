package org.uniupo.it.model;

public class Fault {
    private FaultType faultType;
    private String details;

    public Fault(FaultType faultType, String details) {
        this.faultType = faultType;
        this.details = details;
    }

    public FaultType getFaultType() {
        return faultType;
    }

    public String getDetails() {
        return details;
    }

    public void setFaultType(FaultType faultType) {
        this.faultType = faultType;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Fault{" +
                "faultType=" + faultType +
                ", details='" + details + '\'' +
                '}';
    }

}


