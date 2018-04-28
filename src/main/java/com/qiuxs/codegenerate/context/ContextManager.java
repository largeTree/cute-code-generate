package com.qiuxs.codegenerate.context;

public class ContextManager {

	private static String userName;
	private static String password;
	private static String host;
	private static int port;
	private static String database;

	public static String getUserName() {
		return userName;
	}

	public static void setUserName(String userName) {
		ContextManager.userName = userName;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		ContextManager.password = password;
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		ContextManager.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		ContextManager.port = port;
	}

	public static String getDatabase() {
		return database;
	}

	public static void setDatabase(String database) {
		ContextManager.database = database;
	}

}
