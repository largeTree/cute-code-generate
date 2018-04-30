package com.qiuxs.codegenerate.context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.qiuxs.codegenerate.utils.ComnUtils;

public class DatabaseContext {

	private static Optional<Connection> conn = null;
	private static String currentDatabase = null;

	public static Optional<Connection> getInformationSchemaConnection() {
		currentDatabase = "information_schema";
		String url = "jdbc:mysql://" + ContextManager.getHost() + ":" + ContextManager.getPort() + "/" + currentDatabase;
		try {
			return Optional.ofNullable(DriverManager.getConnection(url, ContextManager.getUserName(), ContextManager.getPassword()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> getAllSchemas() {
		Optional<Connection> informationSchemaConnection = null;
		Optional<Statement> statement = null;
		Optional<ResultSet> rs = null;
		List<String> allSchema = new ArrayList<>();
		try {
			informationSchemaConnection = getInformationSchemaConnection();
			statement = Optional.ofNullable(informationSchemaConnection.get().createStatement());
			rs = Optional.ofNullable(statement.get().executeQuery("SELECT SCHEMA_NAME FROM `SCHEMATA` WHERE SCHEMA_NAME NOT IN ('information_schema','performance_schema','sys','mysql')"));
			rs.ifPresent(r -> {
				try {
					while (r.next()) {
						allSchema.add(r.getString(1));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(informationSchemaConnection);
			close(statement);
			close(rs);
		}
		return allSchema;
	}

	public static void close(Optional<? extends AutoCloseable> closeable) {
		closeable.ifPresent(cls -> {
			try {
				cls.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 获取连接
	 * @param database
	 * 	目标数据库
	 * @return
	 */
	public static Connection getConnection(String database) {
		try {
			Connection tconn = null;
			if (conn.isPresent() && !conn.get().isClosed()) {
				tconn = conn.get();
			} else {
				conn = getInformationSchemaConnection();
				tconn = conn.get();
			}
			// 目标数据库不为空时  切换一下数据库
			if (ComnUtils.isNotBlank(database)) {
				Optional<Statement> statement = Optional.ofNullable(tconn.createStatement());
				statement.get().execute("use " + database + ";");
				currentDatabase = database;
				close(statement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
