package org.uniupo.it.dao;

public final class SQLQueries {
    private SQLQueries() {
    }

    public static final class Machine {
        public static final String GET_MACHINE_STATUS = """
                SELECT "faultStatus" FROM machine."Machine";""";
    }
}
