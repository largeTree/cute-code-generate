package com.qiuxs.codegenerate.context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.qiuxs.codegenerate.utils.ComnUtils;

public class DatabaseContext {

	private static Logger log = Logger.getLogger(DatabaseContext.class);

	private static final String SELECT_SCHEMA_SQL = "SELECT SCHEMA_NAME FROM `SCHEMATA` WHERE SCHEMA_NAME NOT IN ('information_schema','performance_schema','sys','mysql')";
	private static final String SELECT_TABLES_CURRENT_SCHEMA = "SELECT table_name FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = DATABASE()";
	private static Optional<Connection> conn = Optional.empty();
	private static String currentSchema = null;

	public static List<String> getAllSchemas() throws SQLException {
		Connection informationSchemaConnection = null;
		Optional<Statement> statement = null;
		Optional<ResultSet> rs = null;
		List<String> allSchema = new ArrayList<>();
		informationSchemaConnection = getConnection("information_schema");
		statement = Optional.ofNullable(informationSchemaConnection.createStatement());
		rs = Optional.ofNullable(statement.get().executeQuery(SELECT_SCHEMA_SQL));
		rs.ifPresent(r -> {
			try {
				while (r.next()) {
					allSchema.add(r.getString(1));
				}
			} catch (SQLException e) {
				log.error("find schemas failed", e);
			}
		});
		return allSchema;
	}

	public static List<String> getAllTablesBySchema(String schema) throws SQLException {
		currentSchema = schema;
		Connection conn = getConnection(schema);
		List<String> tableNames = new ArrayList<>();
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(SELECT_TABLES_CURRENT_SCHEMA);
		while (rs.next()) {
			tableNames.add(rs.getString(1));
		}
		return tableNames;
	}

	/**
	 * 获取连接
	 *
	 * @param database
	 *            目标数据库
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String schema) throws SQLException {
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
		return conn.get();
	}

	public static Optional<Connection> newConnection(String schema) throws SQLException {
		currentSchema = schema;
		String url = "jdbc:mysql://" + ContextManager.getHost() + ":" + ContextManager.getPort() + "/" + currentSchema;
		log.info("connect to url : " + url);
		return Optional.ofNullable(DriverManager.getConnection(url, ContextManager.getUserName(), ContextManager.getPassword()));
	}

	public static void destory() {
		conn.ifPresent(con -> {
			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				log.error("ext=" + e.getLocalizedMessage(), e);
			}
		});
		conn = Optional.empty();
		currentSchema = null;
	}

	public static void close(Optional<? extends AutoCloseable> closeable) {
		closeable.ifPresent(cls -> {
			try {
				cls.close();
			} catch (Exception e) {
				log.error("ext=" + e.getLocalizedMessage(), e);
			}
		});
	}

	public static String getCurrentSchame() {
		return currentSchema;
	}

}
