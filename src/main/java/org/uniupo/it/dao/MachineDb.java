package org.uniupo.it.dao;

import org.uniupo.it.model.Fault;

import java.util.List;

public interface MachineDb {
    boolean checkMachineStatus();
    List<Fault> checkUpAfterDispense();
}
