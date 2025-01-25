package org.uniupo.it.dao;

import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MachineDbImpl implements MachineDb {
    @Override
    public List<Fault> checkUpAfterDispense() {
        List<Fault> faults = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.Machine.CHECK_CONSUMABLES);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                   /** faults.add(new Fault(
                            FaultType.CONSUMABILE_TERMINATO,
                            rs.getString("name") + " is empty"
                    ));*/
                }
            }

            return faults;
        } catch (SQLException e) {
            throw new RuntimeException("Error during machine checkup", e);
        }
    }

    @Override
    public boolean checkMachineStatus() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLQueries.Machine.GET_MACHINE_STATUS);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBoolean("faultStatus");
            } else {
                throw new RuntimeException("Machine status not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking machine status", e);
        }
    }
}