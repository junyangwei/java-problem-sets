package jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 使用 JDBC 连接 MySQL —— 使用 Hikari 连接池来改进
 * @author junyangwei
 * @date 2021-10-24
 */
public class HikariJDBCTest {

    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/notes");
        config.setUsername("notes");
        config.setPassword("test");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try (HikariDataSource ds = new HikariDataSource(config)) {
            System.out.println("#### 连接数据库 ####");
            Connection conn = ds.getConnection();

            // 执行查语句 (查询用户表信息，并打印）
            JDBCTest.selectTest(conn);

            // 执行写语句（写一条测试数据，并打印结果）
            JDBCTest.insertTest(conn);

            // 执行更新语句
            JDBCTest.updateTest(conn);

            // 执行删除操作
            JDBCTest.deleteTest(conn);

            // 使用 Prepared Statements 事务写入成功例子
            JDBCTest.updateUserSuccess(conn);

            // 使用 Prepared Statements 事务写入失败例子
            JDBCTest.updateUserFail(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
