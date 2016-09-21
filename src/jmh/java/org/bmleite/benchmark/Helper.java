package org.bmleite.benchmark;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Helper {

    // ----------------------------------------------------------------------------------------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(Helper.class);

    public static final String DB_TABLE = "DB_OBJECT";

    // ----------------------------------------------------------------------------------------------------------------

    public static DataSource getDataSource(DbEngine dbEngine) {
        switch (dbEngine) {
            case H2:
                return h2DataSource();
            default:
                throw new IllegalArgumentException("No implementation for db engine " + dbEngine.name());
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    private static HikariDataSource h2DataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("url", "jdbc:h2:mem:test");
        config.addDataSourceProperty("user", "user");
        config.addDataSourceProperty("password", "pass");
        return new HikariDataSource(config);
    }

    // ----------------------------------------------------------------------------------------------------------------

    public static void createAndBootstrapDbTable(DbEngine dbEngine, Connection connection, int numRows) throws SQLException {
        connection.setReadOnly(false);

        if (!tableExists(connection)) {
            LOG.debug("Table does not exist. Creating...");
            // create db
            createTable(dbEngine, connection);
        }

        LOG.debug("Bootstrapping table rows...");
        // bootstrap db
        bootstrapTable(connection, numRows);

        connection.setReadOnly(true);
    }

    private static boolean tableExists(Connection connection) {
        try (Statement st = connection.createStatement()) {
            return st.execute("select count(*) from " + DB_TABLE);
        } catch (SQLException e) {
            return false;
        }
    }

    private static void bootstrapTable(Connection connection, int numRows) {
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("select count(*) from " + DB_TABLE);
            rs.next();
            LOG.debug("Found {} db rows", rs.getLong(1));
            if (rs.getLong(1) == numRows) {
                return;
            }

            // reset
            st.execute("delete from " + DB_TABLE);

            try (PreparedStatement ps = connection.prepareStatement("insert into " + DB_TABLE + " values(?, ?, ?, ?, ?)")) {
                for (int i = 0; i < numRows; i++) {
                    String uuid = UUID.randomUUID().toString();
                    ps.setLong(1, i);
                    ps.setString(2, uuid + "_" + i);
                    ps.setString(3, uuid + "_" + i);
                    ps.setString(4, uuid + "_" + i);
                    ps.setString(5, uuid + "_" + i);
                    ps.addBatch();
                }
                ps.executeBatch();

                rs = st.executeQuery("select count(*) from " + DB_TABLE);
                rs.next();
                LOG.debug("After bootstrap there are {} db rows", rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot setup DB table", e);
        }
    }

    private static void createTable(DbEngine dbEngine, Connection connection) {
        try (Statement st = connection.createStatement()) {
            st.execute(dbEngine.createTableQuery());
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot create DB table", e);
        }
    }

}
