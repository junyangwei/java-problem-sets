package jdbc;

import jdbc.common.OrderUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 批量写入100万条订单数据
 *  - 开启十个线程，分别连接数据库，插入十万条数据
 * @author junyangwei
 * @date 2021-11-13
 */
public class BatchInsertOrdersTest extends JDBCTest{
    // MySQL 8.0 以上版本 JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVE = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/sell?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // 数据库用户名和密码
    static final String USER = "root";
    static final String PASS = "123456";

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(BatchInsertOrdersTest::starter);
            thread.start();
        }
    }

    public static void starter() {
        Connection conn = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVE).newInstance();

            // 与数据库建立连接
            System.out.println("#### 连接数据库 ####" + Thread.currentThread().getName());
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 批量写入10万条订单记录
            batchInsertOrders(conn);

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
     * 批量写入10万条订单记录
     * 参考：https://developer.aliyun.com/article/131478
     */
    public static void batchInsertOrders(Connection conn) {
        String sql = "INSERT INTO orders"
                + " (user_id, order_id, original_amount, amount, order_status, STATUS)"
                + " VALUES"
                + "(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement prep = conn.prepareStatement(sql)) {
            // 不自动提交，强制需要手动提交
            conn.setAutoCommit(false);

            long beginTime = System.currentTimeMillis();

            for (int i = 0; i < 100000; i++) {
                prep.setInt(1, 1);
                prep.setLong(2, OrderUtil.orderIdGenerator());
                prep.setDouble(3, 0);
                prep.setDouble(4, 0);
                prep.setInt(5, 1);
                prep.setInt(6, 1);
                prep.addBatch();
            }

            prep.executeBatch();
            prep.clearBatch();

            conn.commit();

            System.out.println("事务写入结果：" + prep);
            System.out.println(Thread.currentThread().getName() + " 线程耗时(毫秒)：" + (System.currentTimeMillis() - beginTime));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
