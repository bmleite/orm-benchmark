package org.bmleite.benchmark.activejdbc;

import org.bmleite.benchmark.Environment;
import org.bmleite.model.ActiveDbObject;
import org.bmleite.model.DbObject;
import org.javalite.activejdbc.DB;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class ActiveJdbcBenchmark {

    private ModelMapper<DbObject> mapper;

    @Setup
    public void setup(Environment env) {
        new DB("default").open(env.dataSource);
        mapper = model -> {
            DbObject object = new DbObject();
            object.setId(model.getLongId());
            object.setProp1((String) model.get("prop1"));
            object.setProp2((String) model.get("prop2"));
            object.setProp3((String) model.get("prop3"));
            object.setProp4((String) model.get("prop4"));
            return object;
        };
    }

    @Benchmark
    public void measureSelectAll(Environment env, Blackhole blackhole) throws SQLException {
        List<DbObject> objects = ActiveDbObject.findAll()
                .limit(env.totalRows)
                .stream()
                .map(mapper::map)
                .collect(Collectors.toList());

        blackhole.consume(objects);
    }

}
