package org.uniupo.it.dao;

import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public List<Fault> solveGenericFaults() {
        Connection conn = null;
        List<Fault> resolvedFaults = new ArrayList<>();

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement getFaultsStmt = conn.prepareStatement(SQLQueries.Machine.GET_GENERIC_FAULTS)) {
                getFaultsStmt.setObject(1, FaultType.GUASTO_GENERICO.name(), Types.OTHER);
                ResultSet rs = getFaultsStmt.executeQuery();

                while (rs.next()) {

                    resolvedFaults.add(new Fault(
                                    rs.getString("description"),
                                    rs.getObject("id_fault", UUID.class),
                                    rs.getTimestamp("timestamp"),
                                    FaultType.valueOf(rs.getString("fault_type")),
                                    rs.getBoolean("risolto")
                            )
                    );
                }
            }

            if (!resolvedFaults.isEmpty()) {
                try (PreparedStatement updateFaultsStmt = conn.prepareStatement(SQLQueries.Machine.UPDATE_GENERIC_FAULTS)) {
                    updateFaultsStmt.setString(1, FaultType.GUASTO_GENERICO.name());
                    updateFaultsStmt.executeUpdate();

                    try (PreparedStatement updateMachineStmt = conn.prepareStatement(
                            SQLQueries.Machine.UPDATE_MACHINE_STATUS_NO_FAULT)) {
                        updateMachineStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            return resolvedFaults;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            System.out.println("Error solving generic faults: " + e.getMessage());
            throw new RuntimeException("Error solving generic faults", e.getCause());

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