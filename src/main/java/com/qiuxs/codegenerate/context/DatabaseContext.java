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
	private static final String SELECT_SCHEMA_SQL = "SELECT SCHEMA_NAME FROM `SCHEMATA` WHERE SCHEMA_NAME NOT IN ('information_schema','performance_schema','sys','mysql')";
	private static final String SELECT_TABLES_CURRENT_SCHEMA = "SELECT table_name FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = DATABASE()";
	private static Optional<Connection> conn = Optional.empty();
	private static String currentSchema = null;

	public static List<String> getAllSchemas() {
		Connection informationSchemaConnection = null;
		Optional<Statement> statement = null;
		Optional<ResultSet> rs = null;
		List<String> allSchema = new ArrayList<>();
		try {
			informationSchemaConnection = getConnection("information_schema");
			statement = Optional.ofNullable(informationSchemaConnection.createStatement());
			rs = Optional.ofNullable(statement.get().executeQuery(SELECT_SCHEMA_SQL));
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
			close(statement);
			close(rs);
		}
		return allSchema;
	}

	public static List<String> getAllTablesBySchema(String schema) {
		currentSchema = schema;
		Connection conn = getConnection(schema);
		List<String> tableNames = new ArrayList<>();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(SELECT_TABLES_CURRENT_SCHEMA);
			while (rs.next()) {
				tableNames.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableNames;
	}

	/**
	 * 获取连接
	 *
	 * @param database
	 *            目标数据库
	 * @return
	 */
	public static Connection getConnection(String schema) {
		try {
			Connection tconn = null;
			if (conn.isPresent() && !conn.get().isClosed()) {
				tconn = conn.get();
			} else {
				conn = newConnection(schema);
				tconn = conn.get();
			}
			if (schema == null) {
				schema = currentSchema;
			}
			// 目标数据库不为空时 切换一下数据库
			if (ComnUtils.isNotBlank(schema)) {
				Optional<Statement> statement = Optional.ofNullable(tconn.createStatement());
				statement.get().execute("use " + schema + ";");
				if (schema != null) {
					currentSchema = schema;
				}
				close(statement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn.get();
	}

	public static Optional<Connection> newConnection(String schema) {
		currentSchema = schema;
		String url = "jdbc:mysql://" + ContextManager.getHost() + ":" + ContextManager.getPort() + "/" + currentSchema;
		try {
			return Optional.ofNullable(
					DriverManager.getConnection(url, ContextManager.getUserName(), ContextManager.getPassword()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void destory() {
		conn.ifPresent(c -> {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
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

	public static String getCurrentSchame() {
		return currentSchema;
	}

}
