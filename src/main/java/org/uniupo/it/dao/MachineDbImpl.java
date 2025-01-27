package org.uniupo.it.dao;

import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultType;

import java.sql.*;
import java.util.List;

public class MachineDbImpl implements MachineDb {

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

    @Override
    public void insertFaults(List<Fault> faults) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.Machine.INSERT_FAULTS)) {

            for (Fault fault : faults) {
                pstmt.setString(1, fault.getDescription());
                pstmt.setObject(2, fault.getIdFault());
                pstmt.setTimestamp(3, fault.getTimestamp());
                pstmt.setObject(4, fault.getFaultType().toString(), Types.OTHER);
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            System.out.println("Failed to insert faults" + e.getMessage());
            throw new RuntimeException("Failed to insert faults", e);
        }
    }

    @Override
    public void setMachineStatus(boolean status) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.Machine.SET_MACHINE_STATUS)) {
            pstmt.setBoolean(1, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting machine status", e);
        }
    }

    @Override
    public void solveGenericFaults() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement updateFaultsStmt = conn.prepareStatement(SQLQueries.Machine.UPDATE_GENERIC_FAULTS)) {
                updateFaultsStmt.setString(1, FaultType.GUASTO_GENERICO.name());
                int updatedRows = updateFaultsStmt.executeUpdate();

                if (updatedRows > 0) {
                    try (PreparedStatement updateMachineStmt = conn.prepareStatement(
                            SQLQueries.Machine.UPDATE_MACHINE_STATUS_NO_FAULT)) {
                        updateMachineStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            System.err.println("Error solving generic faults: " + e.getMessage());
            throw new RuntimeException("Error solving generic faults", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}