package com.example.aipartner.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 自定义的 MyBatis TypeHandler，用于处理 JSON 格式的 Map 数据。
 * 该 TypeHandler 可以将 Java 中的 Map 对象序列化为 JSON 字符串存储到数据库中，
 * 并在从数据库中读取 JSON 字符串时将其反序列化为 Map 对象。
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Map.class)
public class JsonTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    /**
     * 用于 JSON 序列化和反序列化的 ObjectMapper 实例。
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 Java 中的 Map 对象序列化为 JSON 字符串，并设置到 PreparedStatement 中。
     *
     * @param ps          PreparedStatement 对象
     * @param i           参数索引
     * @param parameter   要序列化的 Map 对象
     * @param jdbcType    JDBC 类型
     * @throws SQLException 如果发生 SQL 异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将 Map 对象序列化为 JSON 字符串
            String json = objectMapper.writeValueAsString(parameter);
            // 设置 JSON 字符串到 PreparedStatement 中
            ps.setString(i, json);
        } catch (Exception e) {
            // 抛出运行时异常，包含详细的错误信息
            throw new RuntimeException("Failed to serialize JSON", e);
        }
    }

    /**
     * 从 ResultSet 中读取 JSON 字符串，并将其反序列化为 Map 对象。
     *
     * @param rs          ResultSet 对象
     * @param columnName  列名
     * @return 反序列化后的 Map 对象
     * @throws SQLException 如果发生 SQL 异常
     */
    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            // 从 ResultSet 中读取 JSON 字符串
            String json = rs.getString(columnName);
            // 将 JSON 字符串反序列化为 Map 对象
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            // 抛出运行时异常，包含详细的错误信息
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }

    /**
     * 从 ResultSet 中读取 JSON 字符串，并将其反序列化为 Map 对象。
     *
     * @param rs          ResultSet 对象
     * @param columnIndex 列索引
     * @return 反序列化后的 Map 对象
     * @throws SQLException 如果发生 SQL 异常
     */
    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            // 从 ResultSet 中读取 JSON 字符串
            String json = rs.getString(columnIndex);
            // 将 JSON 字符串反序列化为 Map 对象
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            // 抛出运行时异常，包含详细的错误信息
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }

    /**
     * 从 CallableStatement 中读取 JSON 字符串，并将其反序列化为 Map 对象。
     *
     * @param cs          CallableStatement 对象
     * @param columnIndex 列索引
     * @return 反序列化后的 Map 对象
     * @throws SQLException 如果发生 SQL 异常
     */
    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            // 从 CallableStatement 中读取 JSON 字符串
            String json = cs.getString(columnIndex);
            // 将 JSON 字符串反序列化为 Map 对象
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            // 抛出运行时异常，包含详细的错误信息
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }
}
