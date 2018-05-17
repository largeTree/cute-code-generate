package com.qiuxs.codegenerate.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.qiuxs.codegenerate.context.CodeTemplateContext;
import com.qiuxs.codegenerate.context.ContextManager;
import com.qiuxs.codegenerate.context.DatabaseContext;
import com.qiuxs.codegenerate.model.TableModel;
import com.qiuxs.codegenerate.utils.ComnUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
	private ListView<Pane> tableList;
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
	private CheckBox entityCkBox;
	@FXML
	private CheckBox daoCkBox;
	@FXML
	private CheckBox mapperCkBox;
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
			for (String tableName : tables) {
				this.tableList.getItems().add(getTablePane(tableName));
			}
		});

		// 选择表
		this.tableList.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			// 将控件的值刷新到模型中
			MainController.this.refreshTableModel();
			// 获取表名
			String tableName = newVal.getUserData().toString();
			// 获取表模型
			TableModel tableModel = CodeTemplateContext.getOrCreateTableModel(tableName);
			// 设置为当前表模型
			this.currentTableModel = tableModel;
			// 设置当前表是否需要构建
			this.currentTableModel.setBuildFlag(((CheckBox) newVal.getChildren().get(0)).isSelected());
			// 还未设置过类名的情况下，自动生成一个类名
			if (ComnUtils.isBlank(this.currentTableModel.getClassName())) {
				this.currentTableModel.setClassName(ComnUtils.firstToUpperCase(ComnUtils.formatName(tableName)));
			}
			// 还未设置过包名的情况下 自动生成一个包名
			if (ComnUtils.isBlank(this.currentTableModel.getPackageName())) {
				this.currentTableModel.setPackageName("com." + this.author.getText() + ".");
			}
			// 刷新控件
			this.refreshControl();
		});

		this.tableControls.add(this.packageName);
		this.tableControls.add(this.superClass);
		this.tableControls.add(this.className);
		this.tableControls.add(this.desc);
		this.tableControls.add(this.entityCkBox);
		this.tableControls.add(this.daoCkBox);
		this.tableControls.add(this.mapperCkBox);
		this.tableControls.add(this.serviceCkBox);
		this.tableControls.add(this.controllerCkBox);

		// 默认所有控件禁用，选择表并勾选build后启用
		this.setDisableFlag(true);

		// 默认输出路径
		this.outPutPathInput.setText(System.getProperty("user.home") + "\\Desktop");
		ContextManager.setOutPutPath(this.outPutPathInput.getText());
		// 默认作者
		this.author.setText(System.getProperty("user.name"));

		// 初始化数据库信息
		initDatabaseInfo();
	}

	private Pane getTablePane(String tableName) {
		// 选择框
		CheckBox tbCk = new CheckBox();
		tbCk.setText("");
		tbCk.setUserData(tableName);
		tbCk.selectedProperty().addListener(new tableBoxChangedListener());
		// 表名显示文字
		Label tableNameLabel = new Label(tableName);
		tableNameLabel.setLayoutX(tbCk.getFont().getSize() * 2);

		// 容器
		Pane pane = new Pane();
		pane.getChildren().add(tbCk);
		pane.getChildren().add(tableNameLabel);
		pane.setUserData(tableName);
		return pane;
	}

	@FXML
	public void connBtnClick(MouseEvent event) {
		initDatabaseInfo();
		if (ContextManager.isComplete()) {
			List<String> allSchemas = DatabaseContext.getAllSchemas();
			schemaCmb.getItems().clear();
			schemaCmb.getItems().addAll(allSchemas);
			if (allSchemas.size() > 0) {
				schemaCmb.getSelectionModel().select(0);
			}
		} else {
			ContextManager.showAlert("数据库信息不完整！！！");
		}
	}

	private void initDatabaseInfo() {
		String userName = this.userInput.getText();
		String password = this.passInput.getText();
		String host = this.hostInput.getText();
		String port = this.portInput.getText();
		ContextManager.setUserName(userName);
		ContextManager.setPassword(password);
		ContextManager.setHost(host);
		ContextManager.setPort(port);
	}

	@FXML
	public void buildBtnClick(MouseEvent event) {
		if (ContextManager.isComplete() && ComnUtils.isBlank(DatabaseContext.getCurrentSchame())) {
			return;
		}
		this.refreshTableModel();
		ContextManager.startBuilder();
	}

	@FXML
	public void outPutPathBtnClick(MouseEvent event) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		File outPutDir = dirChooser.showDialog(ContextManager.getPrimaryStage());
		if (outPutDir != null) {
			this.outPutPathInput.setText(outPutDir.getAbsolutePath());
			ContextManager.setOutPutPath(this.outPutPathInput.getText());
		}
	}

	private void refreshControl() {
		String authorName = this.currentTableModel.getAuthor();
		if (authorName != null) {
			this.author.setText(authorName);
		}
		this.setDisableFlag(!this.currentTableModel.isBuildFlag());
		this.packageName.setText(this.currentTableModel.getPackageName());
		this.superClass.setText(this.currentTableModel.getSuperClass());
		this.className.setText(this.currentTableModel.getClassName());
		this.desc.setText(this.currentTableModel.getDesc());
		this.entityCkBox.setSelected(this.currentTableModel.isEntity());
		this.daoCkBox.setSelected(this.currentTableModel.isDao());
		this.mapperCkBox.setSelected(this.currentTableModel.isMapper());
		this.serviceCkBox.setSelected(this.currentTableModel.isService());
		this.controllerCkBox.setSelected(this.currentTableModel.isController());
	}

	private void refreshTableModel() {
		if (this.currentTableModel == null) {
			return;
		}
		this.currentTableModel.setAuthor(this.author.getText());
		this.currentTableModel.setPackageName(this.packageName.getText());
		this.currentTableModel.setSuperClass(this.superClass.getText());
		this.currentTableModel.setClassName(this.className.getText());
		this.currentTableModel.setDesc(this.desc.getText());
		this.currentTableModel.setEntity(this.entityCkBox.isSelected());
		this.currentTableModel.setDao(this.daoCkBox.isSelected());
		this.currentTableModel.setMapper(this.mapperCkBox.isSelected());
		this.currentTableModel.setService(this.serviceCkBox.isSelected());
		this.currentTableModel.setController(this.controllerCkBox.isSelected());
	}

	private void setDisableFlag(boolean flag) {
		this.tableControls.forEach(control -> {
			control.setDisable(flag);
		});
	}

	class tableBoxChangedListener implements ChangeListener<Boolean> {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			MainController.this.setDisableFlag(oldValue);
			MainController.this.currentTableModel.setBuildFlag(newValue);
		}

	}
}
