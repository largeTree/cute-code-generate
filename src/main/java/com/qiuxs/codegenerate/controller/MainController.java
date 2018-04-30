package com.qiuxs.codegenerate.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.qiuxs.codegenerate.context.ContextManager;
import com.qiuxs.codegenerate.context.DatabaseContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MainController implements Initializable {

	@FXML
	private TextField userInput;
	@FXML
	private PasswordField passInput;
	@FXML
	private TextField hostInput;
	@FXML
	private TextField portInput;
	@FXML
	private Button connBtn;
	@FXML
	private ComboBox<String> schemaCmb;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		schemaCmb.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {

		});
	}

	@FXML
	public void ConnBtnClick(MouseEvent event) {
		String userName = this.userInput.getText();
		String password = this.passInput.getText();
		String host = this.hostInput.getText();
		String port = this.portInput.getText();
		ContextManager.setUserName(userName);
		ContextManager.setPassword(password);
		ContextManager.setHost(host);
		ContextManager.setPort(port);
		if (ContextManager.isComplete()) {
			List<String> allSchemas = DatabaseContext.getAllSchemas();
			schemaCmb.getItems().clear();
			schemaCmb.getItems().addAll(allSchemas);
			if (allSchemas.size() > 0) {
				schemaCmb.getSelectionModel().select(0);
			}
		}
	}

}
