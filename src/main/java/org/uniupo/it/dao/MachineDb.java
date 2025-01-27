package org.uniupo.it.dao;

import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultMessage;

import java.util.List;

public interface MachineDb {
    boolean checkMachineStatus();
    void setMachineStatus(boolean status);
    void insertFaults(List<Fault> faults);
    void solveGenericFaults();
}
