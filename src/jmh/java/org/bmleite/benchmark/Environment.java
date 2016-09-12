package org.bmleite.benchmark;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@State(Scope.Benchmark)
public class Environment {

    // ----------------------------------------------------------------------------------------------------------------

    @Param(value="H2")
    public DbEngine dbEngine;

    @Param(value = {"10", "100", "1000", "10000", "100000"})
    public int totalRows;

    public DataSource dataSource;

    // ----------------------------------------------------------------------------------------------------------------

    @Setup
    public void init() throws SQLException, NamingException {
        dataSource = Helper.getDataSource(dbEngine);

        try (Connection connection = dataSource.getConnection()) {
            Helper.createAndBootstrapDbTable(dbEngine, connection, totalRows);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
