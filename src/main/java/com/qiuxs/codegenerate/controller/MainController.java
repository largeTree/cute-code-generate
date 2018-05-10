package com.qiuxs.codegenerate.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import com.qiuxs.codegenerate.TableBuilderThread;
import com.qiuxs.codegenerate.context.CodeTemplateContext;
import com.qiuxs.codegenerate.context.ContextManager;
import com.qiuxs.codegenerate.context.DatabaseContext;
import com.qiuxs.codegenerate.model.TableModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

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
	private Button buildBtn;
	@FXML
	private ComboBox<String> schemaCmb;
	@FXML
	private ListView<String> tableList;
	@FXML
	private TextField outPutPathInput;
	@FXML
	private Button outPutPathChooserBtn;
	@FXML
	private TextField author;
	@FXML
	private TextField packageName;
	@FXML
	private TextField superClass;
	@FXML
	private TextField className;
	@FXML
	private TextField desc;
	@FXML
	private CheckBox buildFlag;
	@FXML
	private CheckBox entityCkBox;
	@FXML
	private CheckBox daoCkBox;
	@FXML
	private CheckBox serviceCkBox;
	@FXML
	private CheckBox controllerCkBox;

	private TableModel currentTableModel;

	private List<Control> tableControls = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 选择数据库
		this.schemaCmb.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			this.tableList.getItems().clear();
			List<String> tables = DatabaseContext.getAllTablesBySchema(newVal);
			this.tableList.getItems().addAll(tables);
		});

		// 选择表
		this.tableList.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			this.refreshTableModel();
			TableModel tableModel = CodeTemplateContext.getOrCreateTableModel(newVal);
			this.currentTableModel = tableModel;
			this.refreshControl();
			// 启用构建开关
			this.buildFlag.setDisable(false);
		});

		// 构建状态变化
		this.buildFlag.selectedProperty().addListener((observable, oldVal, newVal) -> {
			this.setDisableFlag(oldVal);
			this.currentTableModel.setBuildFlag(newVal);
		});

		// 默认构建选项不可用，选择表后启用
		this.buildFlag.setDisable(true);

		this.tableControls.add(this.packageName);
		this.tableControls.add(this.superClass);
		this.tableControls.add(this.className);
		this.tableControls.add(this.desc);
		this.tableControls.add(this.entityCkBox);
		this.tableControls.add(this.daoCkBox);
		this.tableControls.add(this.serviceCkBox);
		this.tableControls.add(this.controllerCkBox);

		// 默认所有控件禁用，选择表并勾选build后启用
		this.setDisableFlag(true);
	}

	@FXML
	public void connBtnClick(MouseEvent event) {
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

	@FXML
	public void buildBtnClick(MouseEvent event) {
		// 显示进度条
		CountDownLatch latch = new CountDownLatch(1);
		new TableBuilderThread(latch).start();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 关闭进度条
	}

	@FXML
	public void outPutPathBtnClick(MouseEvent event) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		File outPutDir = dirChooser.showDialog(ContextManager.getPrimaryStage());
		if (outPutDir != null) {
			this.outPutPathInput.setText(outPutDir.getAbsolutePath());
		}
	}

	private void refreshControl() {
		this.buildFlag.setSelected(this.currentTableModel.isBuildFlag());
		this.setDisableFlag(!this.currentTableModel.isBuildFlag());
		this.packageName.setText(this.currentTableModel.getPackageName());
		this.superClass.setText(this.currentTableModel.getSuperClass());
		this.className.setText(this.currentTableModel.getClassName());
		this.desc.setText(this.currentTableModel.getDesc());
		this.entityCkBox.setSelected(this.currentTableModel.isEntity());
		this.daoCkBox.setSelected(this.currentTableModel.isDao());
		this.serviceCkBox.setSelected(this.currentTableModel.isService());
		this.controllerCkBox.setSelected(this.currentTableModel.isController());
	}

	private void refreshTableModel() {
		if (this.currentTableModel == null) {
			return;
		}
		this.currentTableModel.setBuildFlag(this.buildFlag.isSelected());
		this.currentTableModel.setPackageName(this.packageName.getText());
		this.currentTableModel.setSuperClass(this.superClass.getText());
		this.currentTableModel.setClassName(this.className.getText());
		this.currentTableModel.setDesc(this.desc.getText());
		this.currentTableModel.setEntity(this.entityCkBox.isSelected());
		this.currentTableModel.setDao(this.daoCkBox.isSelected());
		this.currentTableModel.setService(this.serviceCkBox.isSelected());
		this.currentTableModel.setController(this.controllerCkBox.isSelected());
	}

	private void setDisableFlag(boolean flag) {
		this.tableControls.forEach(control -> {
			control.setDisable(flag);
		});
	}

}
