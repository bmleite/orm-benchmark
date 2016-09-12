package org.bmleite.benchmark.jdbc;

import org.bmleite.model.DbObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbObjectRowMapper implements RowMapper<DbObject> {

    @Override
    public DbObject map(ResultSet rs) throws SQLException {
        DbObject object = new DbObject();
        object.setId(rs.getLong(1));
        object.setProp1(rs.getString(2));
        object.setProp2(rs.getString(3));
        object.setProp3(rs.getString(4));
        object.setProp4(rs.getString(5));
        return object;
    }
}
