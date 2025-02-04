package org.uniupo.it.dao;

public final class SQLQueries {
    private SQLQueries() {
    }

    public static String getSchemaName(String instituteId, String machineId) {
        return String.format("machine_%s_%s",
                instituteId.toLowerCase().replace("-", "_"),
                machineId.toLowerCase().replace("-", "_"));
    }

    public static final class Machine {
        private static final String GET_MACHINE_STATUS_TEMPLATE = """
                SELECT "faultStatus" FROM %s."Machine";""";

        private static final String CHECK_CONSUMABLES_TEMPLATE = """
                SELECT name, quantity, "maxQuantity"
                FROM %s.consumable
                WHERE quantity = 0;""";

        private static final String INSERT_FAULTS_TEMPLATE = """
                INSERT INTO %s."Fault" (description, id_fault, timestamp, fault_type)
                VALUES (?, ?, ?, ?)""";

        private static final String GET_FAULTS_TEMPLATE = """
                SELECT description, id_fault, timestamp, fault_type, risolto
                FROM %s."Fault" WHERE risolto IS FALSE;""";

        private static final String SET_MACHINE_STATUS_TEMPLATE = """
                UPDATE %s."Machine" SET "faultStatus" = ?;""";

        private static final String GET_GENERIC_FAULTS_TEMPLATE = """
                SELECT id_fault, description, timestamp, fault_type, risolto
                FROM %s."Fault"
                WHERE fault_type = ?::%s.fault_type AND risolto = ?""";

        private static final String UPDATE_GENERIC_FAULTS_TEMPLATE = """
                UPDATE %s."Fault"
                SET risolto = true
                WHERE id_fault = ANY(?)""";

        private static final String UPDATE_MACHINE_STATUS_NO_FAULT_TEMPLATE = """
                UPDATE %s."Machine" SET "faultStatus" = false""";

        private static final String GET_CONSUMABLE_FAULTS_TEMPLATE = """
                SELECT f.id_fault, f.description, f.timestamp, f.fault_type, f.risolto, c.name, c."maxQuantity"
                FROM %s."Fault" f
                JOIN %s.consumable c ON f.description LIKE '%%' || c.name::text || '%%'
                WHERE f.fault_type = 'CONSUMABILE_TERMINATO'::%s.fault_type
                AND f.risolto = false""";

        private static final String REFILL_CONSUMABLE_TEMPLATE = """
                UPDATE %s.consumable
                SET quantity = "maxQuantity"
                WHERE name = ?::%s."ConsumableType\"""";

        private static final String UPDATE_CONSUMABLE_FAULTS_TEMPLATE = """
                UPDATE %s."Fault"
                SET risolto = true
                WHERE fault_type = 'CONSUMABILE_TERMINATO'::%s.fault_type
                AND risolto = false""";

        // Metodi helper per generare le query con lo schema corretto
        public static String getMachineStatus(String instituteId, String machineId) {
            return String.format(GET_MACHINE_STATUS_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String checkConsumables(String instituteId, String machineId) {
            return String.format(CHECK_CONSUMABLES_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String insertFaults(String instituteId, String machineId) {
            return String.format(INSERT_FAULTS_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String getFaults(String instituteId, String machineId) {
            return String.format(GET_FAULTS_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String setMachineStatus(String instituteId, String machineId) {
            return String.format(SET_MACHINE_STATUS_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String getGenericFaults(String instituteId, String machineId) {
            String schema = getSchemaName(instituteId, machineId);
            return String.format(GET_GENERIC_FAULTS_TEMPLATE, schema, schema);
        }

        public static String updateGenericFaults(String instituteId, String machineId) {
            return String.format(UPDATE_GENERIC_FAULTS_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String updateMachineStatusNoFault(String instituteId, String machineId) {
            return String.format(UPDATE_MACHINE_STATUS_NO_FAULT_TEMPLATE, getSchemaName(instituteId, machineId));
        }

        public static String getConsumableFaults(String instituteId, String machineId) {
            String schema = getSchemaName(instituteId, machineId);
            return String.format(GET_CONSUMABLE_FAULTS_TEMPLATE, schema, schema, schema);
        }

        public static String refillConsumable(String instituteId, String machineId) {
            String schema = getSchemaName(instituteId, machineId);
            return String.format(REFILL_CONSUMABLE_TEMPLATE, schema, schema);
        }

        public static String updateConsumableFaults(String instituteId, String machineId) {
            String schema = getSchemaName(instituteId, machineId);
            return String.format(UPDATE_CONSUMABLE_FAULTS_TEMPLATE, schema, schema);
        }
    }
}