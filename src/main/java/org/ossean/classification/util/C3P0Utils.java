package org.ossean.classification.util;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Utils {
	private static C3P0Utils dbcputils = null;
	private ComboPooledDataSource cpds = null;

	private C3P0Utils() {
		if (cpds == null) {
			cpds = new ComboPooledDataSource();
			cpds.setUser("root");
			//cpds.setPassword("1234");
			cpds.setPassword("123456");
			//cpds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/classification");
			cpds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/datas");
			try {
				cpds.setDriverClass("com.mysql.jdbc.Driver");
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cpds.setInitialPoolSize(2);
			cpds.setMinPoolSize(1);
			cpds.setMaxPoolSize(10);
			cpds.setMaxStatements(50);
			cpds.setMaxIdleTime(60);
		}
	}

	public synchronized static C3P0Utils getInstance() {
		if (dbcputils == null)
			dbcputils = new C3P0Utils();
		return dbcputils;
	}

	public Connection getConnection() {
		Connection con = null;
		try {
			con = cpds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	public static void main(String[] args) throws SQLException {
		Connection con = null;
		long begin = System.currentTimeMillis();
		C3P0Utils c3p0 = C3P0Utils.getInstance();
		for (int i = 0; i < 1000000; i++) {
			con = c3p0.getConnection();
			System.out.println("test");
			con.close();
		}
		long end = System.currentTimeMillis();
		// System.out.println("鑰楁椂涓�:" + (end - begin) + "ms");
	}
}