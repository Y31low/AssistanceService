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

        public static final String GET_GENERIC_FAULTS =
                "SELECT id_fault, description, timestamp, fault_type, risolto " +
                        "FROM machine.\"Fault\" " +
                        "WHERE fault_type = ?::machine.\"fault_type\" AND risolto = ?";

        public static final String UPDATE_GENERIC_FAULTS =
                "UPDATE machine.\"Fault\" " +
                        "SET risolto = true " +
                        "WHERE id_fault = ANY(?)";

        public static final String UPDATE_MACHINE_STATUS_NO_FAULT =
                "UPDATE machine.\"Machine\" SET \"faultStatus\" = false";


        public static final String GET_CONSUMABLE_FAULTS = """
                    SELECT f.id_fault, f.description, f.timestamp, f.fault_type, f.risolto, c.name, c."maxQuantity"
                    FROM machine."Fault" f
                    JOIN machine.consumable c ON f.description LIKE '%' || c.name::text || '%'
                    WHERE f.fault_type = 'CONSUMABILE_TERMINATO'::machine.fault_type\s
                    AND f.risolto = false;
               \s""";

        public static final String REFILL_CONSUMABLE = """
                    UPDATE machine.consumable\s
                    SET quantity = "maxQuantity"
                    WHERE name = ?::machine."ConsumableType";
               \s""";

        public static final String UPDATE_CONSUMABLE_FAULTS = """
                    UPDATE machine."Fault"
                    SET risolto = true
                    WHERE fault_type = 'CONSUMABILE_TERMINATO'::machine.fault_type 
                    AND risolto = false;
                """;
    }
}
