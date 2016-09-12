package org.bmleite.benchmark.jdbc;

import org.bmleite.benchmark.Environment;
import org.bmleite.model.DbObject;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class PlainJdbcBenchmark {

    private RowMapper<DbObject> mapper;

    @Setup
    public void setup() {
        mapper = new DbObjectRowMapper();
    }

    @Benchmark
    public void measureSelectAll(Environment env, Blackhole blackhole) throws SQLException {
        try (Connection connection = env.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(env.dbEngine.selectQuery())) {
                ps.setInt(1, env.totalRows);
                ResultSet rs = ps.executeQuery();
                List<DbObject> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapper.map(rs));
                }
                blackhole.consume(list);
            }
        }
    }

}
