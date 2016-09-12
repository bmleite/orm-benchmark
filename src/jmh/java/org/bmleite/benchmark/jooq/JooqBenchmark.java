package org.bmleite.benchmark.jooq;

import org.bmleite.benchmark.Environment;
import org.bmleite.model.jooq.tables.Dbobject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.SQLException;

@State(Scope.Benchmark)
public class JooqBenchmark {

    private DSLContext dsl;

    @Setup
    public void setup(Environment env) {
        switch (env.dbEngine) {
            case H2:
                dsl = DSL.using(new DefaultConfiguration().set(env.dataSource).set(SQLDialect.H2));
                break;

            default:
                throw new IllegalStateException();
        }
    }

    @Benchmark
    public void measureSelectAll(Environment env, Blackhole blackhole) throws SQLException {
        Result<Record> records = dsl.select().from(Dbobject.DBOBJECT).limit(env.totalRows).fetch();
        blackhole.consume(records);
    }

}
