package org.uniupo.it.dao;

public final class SQLQueries {
    private SQLQueries() {
    }

    public static final class Machine {
        public static final String GET_MACHINE_STATUS = """
                SELECT "faultStatus" FROM machine."Machine";""";

        public static final String CHECK_CONSUMABLES = """
                SELECT name, quantity, "maxQuantity"
                FROM machine.consumable
                WHERE quantity = 0;""";

        public static final String INSERT_FAULTS = """
                INSERT INTO machine."Fault" (description, id_fault, timestamp, fault_type)\s
                VALUES (?, ?, ?, ?)""";

        public static final String GET_FAULTS = """
                SELECT description, id_fault, timestamp, fault_type, risolto
                FROM machine."Fault" WHERE risolto IS FALSE;""";

        public static final String SET_MACHINE_STATUS = """
                UPDATE machine."Machine" SET "faultStatus" = ?;""";

        public static final String UPDATE_GENERIC_FAULTS =
                "UPDATE machine.\"Fault\" SET \"risolto\" = true " +
                        "WHERE \"fault_type\" = ?::machine.\"fault_type\" AND \"risolto\" = false";

        public static final String UPDATE_MACHINE_STATUS_NO_FAULT =
                "UPDATE machine.\"Machine\" SET \"faultStatus\" = false";

        public static final String GET_GENERIC_FAULTS =
                "SELECT id_fault, description, timestamp, fault_type,risolto FROM machine.\"Fault\" WHERE fault_type = ?;";
    }
}
