package com.qiuxs.codegenerate;

import java.io.IOException;

import com.qiuxs.codegenerate.context.ContextManager;
import com.qiuxs.codegenerate.context.DatabaseContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationEntry extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			ContextManager.setPrimaryStage(primaryStage);
			Parent main = FXMLLoader.load(getClass().getResource("/main.fxml"));
			main.getStylesheets().add("/main.css");
			primaryStage.setTitle("代码生成");
			primaryStage.setScene(new Scene(main));
			primaryStage.setOnCloseRequest(event -> {
				DatabaseContext.destory();
				ContextManager.destory();
			});
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
