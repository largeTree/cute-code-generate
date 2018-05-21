package com.qiuxs.codegenerate.context;

import com.qiuxs.codegenerate.utils.ComnUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ContextManager {

	private static Stage primaryStage;

	private static String userName;
	private static String password;
	private static String host;
	private static String port;
	private static String database;

	private static String outPutPath;
	private static String author;
	private static String packageName;

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void setPrimaryStage(Stage primaryStage) {
		ContextManager.primaryStage = primaryStage;
	}

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

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		ContextManager.port = port;
	}

	public static String getDatabase() {
		return database;
	}

	public static void setDatabase(String database) {
		ContextManager.database = database;
	}

	public static String getAuthor() {
		return author;
	}

	public static void setAuthor(String author) {
		ContextManager.author = author;
	}

	public static String getPackageName() {
		return packageName;
	}

	public static void setPackageName(String packageName) {
		ContextManager.packageName = packageName;
	}

	public static void destory() {
	}

	/**
	 * 信息是否完整
	 *
	 * @return
	 */
	public static boolean isComplete() {
		return ComnUtils.isNotBlank(userName) && ComnUtils.isNotBlank(password) && ComnUtils.isNotBlank(host)
				&& ComnUtils.isNotBlank(port);
	}

	public static void setOutPutPath(String outPutPath) {
		ContextManager.outPutPath = outPutPath;
	}

	public static String getOutPutPath() {
		return outPutPath;
	}

	public static void showAlert(String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erro");
		alert.setContentText(content);
		alert.show();
	}

}
