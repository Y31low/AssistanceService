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

        public static final String CHECK_CASH_BOX = """
            SELECT "totalBalance", "maxBalance"
            FROM machine."Machine"
            WHERE "totalBalance" >= "maxBalance" * 0.9;""";
    }
}
