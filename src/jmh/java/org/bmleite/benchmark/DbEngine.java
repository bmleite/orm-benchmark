package org.bmleite.benchmark;

import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLTemplates;

public enum DbEngine {
    H2 {
        @Override
        public String createTableQuery() {
            return "CREATE TABLE " + Helper.DB_TABLE + " (\n" +
                    "  id    BIGINT PRIMARY KEY,\n" +
                    "  prop1 VARCHAR(50),\n" +
                    "  prop3 VARCHAR(50),\n" +
                    "  prop2 VARCHAR(50),\n" +
                    "  prop4 VARCHAR(50)\n" +
                    ");";
        }

        @Override
        public String selectQuery() {
            return "SELECT id, prop1, prop2, prop3, prop4 FROM " + Helper.DB_TABLE + " LIMIT ?";
        }

        @Override
        public SQLTemplates templates() {
            return H2Templates.builder().build();
        }
    };

    public abstract String createTableQuery();

    public abstract String selectQuery();

    public abstract SQLTemplates templates();
}
