package com.shemuel.timeline.tools.mp;

import com.shemuel.timeline.utils.AESUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(String.class)
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    private static final String SECRET_KEY = "mySecretKey";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, AESUtil.encrypt(parameter, SECRET_KEY));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String dbData = rs.getString(columnName);
        return dbData == null ? null : AESUtil.decrypt(dbData, SECRET_KEY);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String dbData = rs.getString(columnIndex);
        return dbData == null ? null : AESUtil.decrypt(dbData, SECRET_KEY);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String dbData = cs.getString(columnIndex);
        return dbData == null ? null : AESUtil.decrypt(dbData, SECRET_KEY);
    }
}
