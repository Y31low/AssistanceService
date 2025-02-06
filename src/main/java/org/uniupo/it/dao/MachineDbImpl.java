package org.uniupo.it.dao;

import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.uniupo.it.dao.SQLQueries.Machine.*;

public class MachineDbImpl implements MachineDb {

    private final String instituteId;
    private final String machineId;

    public MachineDbImpl(String instituteId, String machineId) {
        this.instituteId = instituteId;
        this.machineId = machineId;
    }
    @Override
    public boolean checkMachineStatus() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQLQueries.Machine.getMachineStatus(instituteId, machineId));
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBoolean("faultStatus");
            } else {
                System.err.println("Machine status not found");
                throw new RuntimeException("Machine status not found");
            }
        } catch (SQLException e) {
            System.err.println("Error checking machine status: " + e.getMessage());
            throw new RuntimeException("Error checking machine status", e);
        }
    }

    @Override
    public void insertFaults(List<Fault> faults) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.Machine.insertFaults(instituteId, machineId))) {

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
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.Machine.setMachineStatus(instituteId, machineId))) {
            pstmt.setBoolean(1, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error setting machine status: " + e.getMessage());
            throw new RuntimeException("Error setting machine status", e);
        }
    }

    @Override
    public List<Fault> solveGenericFaults() {
        List<Fault> resolvedFaults = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Ottiene solo i guasti generici non risolti
                try (PreparedStatement getFaultsStmt = conn.prepareStatement(getGenericFaults(instituteId, machineId))) {
                    getFaultsStmt.setString(1, FaultType.GUASTO_GENERICO.name());
                    getFaultsStmt.setBoolean(2, false); // solo guasti non risolti

                    try (ResultSet rs = getFaultsStmt.executeQuery()) {
                        while (rs.next()) {
                            resolvedFaults.add(new Fault(
                                    rs.getString("description"),
                                    rs.getObject("id_fault", UUID.class),
                                    rs.getTimestamp("timestamp"),
                                    FaultType.valueOf(rs.getString("fault_type")),
                                    false
                            ));
                        }
                    }
                }

                if (!resolvedFaults.isEmpty()) {
                    // Aggiorna solo i guasti specifici trovati
                    try (PreparedStatement updateFaultsStmt = conn.prepareStatement(updateGenericFaults(instituteId, machineId))) {
                        Array faultIds = conn.createArrayOf("uuid",
                                resolvedFaults.stream()
                                        .map(Fault::getIdFault)
                                        .toArray());
                        updateFaultsStmt.setArray(1, faultIds);
                        updateFaultsStmt.executeUpdate();
                    }

                    // Aggiorna lo stato delle macchine
                    try (PreparedStatement updateMachineStmt = conn.prepareStatement(updateMachineStatusNoFault(instituteId, machineId))) {
                        updateMachineStmt.executeUpdate();
                    }
                }

                conn.commit();
                return resolvedFaults;

            } catch (SQLException e) {
                System.err.println("Error solving generic faults: " + e.getMessage());
                conn.rollback();
                throw new RuntimeException("Error solving generic faults", e);
            }
        } catch (SQLException e) {
            System.err.println("Error solving generic faults: " + e.getMessage());
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public void deleteMachineSchema() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.Machine.deleteMachineSchema(instituteId, machineId))) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting machine schema: " + e.getMessage());
            throw new RuntimeException("Error deleting machine schema", e);
        }
    }

    @Override
    public List<Fault> handleConsumableFaults() {
        Connection conn = null;
        List<Fault> resolvedFaults = new ArrayList<>();

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement getFaultsStmt = conn.prepareStatement(SQLQueries.Machine.getConsumableFaults(instituteId, machineId));
                 PreparedStatement refillStmt = conn.prepareStatement(SQLQueries.Machine.refillConsumable(instituteId, machineId));
                 PreparedStatement updateFaultsStmt = conn.prepareStatement(SQLQueries.Machine.updateConsumableFaults(instituteId, machineId))) {

                ResultSet rs = getFaultsStmt.executeQuery();

                while (rs.next()) {
                    resolvedFaults.add(new Fault(
                            rs.getString("description"),
                            rs.getObject("id_fault", UUID.class),
                            rs.getTimestamp("timestamp"),
                            FaultType.CONSUMABILE_TERMINATO,
                            false
                    ));

                    refillStmt.setString(1, rs.getString("name"));
                    refillStmt.executeUpdate();
                }

                if (!resolvedFaults.isEmpty()) {
                    updateFaultsStmt.executeUpdate();

                    try (PreparedStatement checkFaultsStmt = conn.prepareStatement(SQLQueries.Machine.getFaults(instituteId, machineId))) {
                        ResultSet remainingFaults = checkFaultsStmt.executeQuery();
                        if (!remainingFaults.next()) {
                            try (PreparedStatement updateMachineStmt = conn.prepareStatement(
                                    updateMachineStatusNoFault(instituteId, machineId))) {
                                updateMachineStmt.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
                return resolvedFaults;

            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            System.err.println("Error handling consumable faults: " + e.getMessage());
            throw new RuntimeException("Error handling consumable faults", e);
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