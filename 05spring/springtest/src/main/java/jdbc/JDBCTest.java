package jdbc;

import jdbc.entity.User;

import java.sql.*;

/**
 * 使用 JDBC 连接 MySQL
 *  - 进行增删查改操作
 *      - 参考：https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-examples.html
 *  - 使用 Prepared Statements 进行简单的事务操作
 *      - 参考：https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
 * @author junyangwei
 * @date 2021-10-24
 */
public class JDBCTest {

    // MySQL 8.0 以上版本 JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVE = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/notes?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // 数据库用户名和密码
    static final String USER = "notes";
    static final String PASS = "test";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVE).newInstance();

            // 与数据库建立连接
            System.out.println("#### 连接数据库 ####");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查语句 (查询用户表信息，并打印）
            selectTest(conn);

            // 执行写语句（写一条测试数据，并打印结果）
            insertTest(conn);

            // 执行更新语句
            updateTest(conn);

            // 执行删除操作
            deleteTest(conn);

            // 使用 Prepared Statements 事务写入成功例子
            updateUserSuccess(conn);

            // 使用 Prepared Statements 事务写入失败例子
            updateUserFail(conn);


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                conn = null;
            }
        }
    }

    /**
     * 执行查语句 (查询用户表信息，并打印）
     * @param conn 与 MySQL 的连接
     */
    public static void selectTest(Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM user WHERE id = 1");
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                System.out.println(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行写语句 (写一条新的用户表信息，并打印结果）
     * @param conn 与 MySQL 的连接
     */
    public static void insertTest(Connection conn) {
        Statement stmt = null;
        int rs = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeUpdate("INSERT INTO user (username, password) VAlUES('test12', 'test1234')");
            System.out.println("写入是否成功: " + rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行更新语句 (更新用户表信息，并打印结果）
     * @param conn 与 MySQL 的连接
     */
    public static void updateTest(Connection conn) {
        Statement stmt = null;
        int rs = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeUpdate("UPDATE user SET nickname = 'test nickname' WHERE id = 1");
            System.out.println("更新操作是否成功: " + rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行删除语句 (删除一条用户表信息，并打印结果）
     *  - 一般业务其实并不会真的删掉一条数据，只是将 status 置为无效
     *  - 这里我是直接使用了删除语句
     * @param conn 与 MySQL 的连接
     */
    public static void deleteTest(Connection conn) {
        Statement stmt = null;
        int rs = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeUpdate("DELETE FROM user WHERE id = 19");
            System.out.println("删除操作是否成功: " + rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 使用 Prepared Statements 模拟简单的事务更新的场景
     *  - 事务写入成功的例子
     */
    public static void updateUserSuccess(Connection conn) {
        String updateNickname =
                "UPDATE user SET nickname = 'test ps' WHERE id = 1";
        String updatePhone =
                "UPDATE user set phone = '19000001000' WHERE id = 2";

        try (PreparedStatement update01 = conn.prepareStatement(updateNickname);
             PreparedStatement update02 = conn.prepareStatement(updatePhone)) {

            // 不自动提交，强制需要手动提交
            conn.setAutoCommit(false);

            int result01 = update01.executeUpdate();
            int result02 = update02.executeUpdate();
            conn.commit();

            System.out.println("事务写入成功结果：" + result01 + ", " + result02);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用 Prepared Statements 模拟简单的事务更新的场景
     *  - 事务写入失败的例子
     */
    public static void updateUserFail(Connection conn) {
        String updateNickname =
                "UPDATE user SET nickname = 'test ps111' WHERE id = 1";
        // 故意制造一个 username 唯一性冲突的错误
        String updateUsername =
                "UPDATE user set username = 'test1234' WHERE id = 1";

        try (PreparedStatement update01 = conn.prepareStatement(updateNickname);
             PreparedStatement update02 = conn.prepareStatement(updateUsername)) {

            // 不自动提交，强制需要手动提交
            conn.setAutoCommit(false);

            int result01 = update01.executeUpdate();
            int result02 = update02.executeUpdate();

            conn.commit();

            System.out.println("事务写入失败结果：" + result01 + ", " + result02);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
