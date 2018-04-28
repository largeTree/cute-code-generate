package com.qiuxs.codegenerate.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.qiuxs.codegenerate.context.ContextManager;

public class DatebaseUtils {

	public static Connection getInformationSchemaConnection() {
		String url = "jdbc:mysql://" + ContextManager.getHost() + ":" + ContextManager.getPort() + "/information_schema";
		try {
			return DriverManager.getConnection(url, ContextManager.getUserName(), ContextManager.getPassword());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
