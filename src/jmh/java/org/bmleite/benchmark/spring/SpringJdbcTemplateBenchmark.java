package org.bmleite.benchmark.spring;

import org.bmleite.benchmark.Environment;
import org.bmleite.benchmark.jdbc.DbObjectRowMapper;
import org.bmleite.model.DbObject;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@State(Scope.Benchmark)
public class SpringJdbcTemplateBenchmark {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<DbObject> mapper;

    @Setup
    public void setup(Environment env) {
        jdbcTemplate = new JdbcTemplate(env.dataSource);
        mapper = new RowMapper<DbObject>() {
            private DbObjectRowMapper dbObjectRowMapper = new DbObjectRowMapper();

            @Override
            public DbObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                return dbObjectRowMapper.map(rs);
            }
        };
    }

    @Benchmark
    public void measureSelectAll(Environment env, Blackhole blackhole) {
        List<DbObject> objects = jdbcTemplate.query(env.dbEngine.selectQuery(), new Object[]{env.totalRows}, mapper);
        blackhole.consume(objects);
    }
}


