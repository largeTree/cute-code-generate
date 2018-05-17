package com.qiuxs.codegenerate.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.qiuxs.codegenerate.context.ContextManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class LoadingController implements Initializable {

	@FXML
	private ImageView loadingImage;

	@FXML
	private Button cancelBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Image img = new Image("./images/loading.gif");
		loadingImage.setImage(img);
	}

	public void cancelBtnClick(MouseEvent event) {
		ContextManager.cancelBuild();
		ContextManager.hideLoading();
	}

}
