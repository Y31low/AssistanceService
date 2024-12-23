package org.uniupo.it.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MachineDbImpl implements MachineDb {
    @Override
    public boolean checkMachineStatus() {
        System.out.println("Checking machine status");
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            System.out.println("Connected to database");
            try (PreparedStatement stmt = conn.prepareStatement(SQLQueries.Machine.GET_MACHINE_STATUS)) {
                System.out.println("Machine status query prepared");
                ResultSet rs = stmt.executeQuery();
                System.out.println("Machine status checked");
                if (rs.next()) {
                    return rs.getBoolean("faultStatus");
                } else {
                    throw new RuntimeException("Machine status not found");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking machine status", e);
        }
    }
}
