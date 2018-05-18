package com.qiuxs.codegenerate.controller;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.qiuxs.codegenerate.context.CodeTemplateContext;
import com.qiuxs.codegenerate.context.ContextManager;
import com.qiuxs.codegenerate.context.DatabaseContext;
import com.qiuxs.codegenerate.model.TableModel;
import com.qiuxs.codegenerate.task.TaskExecuter;
import com.qiuxs.codegenerate.task.TaskResult;
import com.qiuxs.codegenerate.utils.ComnUtils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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

	private static Logger log = Logger.getLogger(MainController.class);

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
			Optional<List<String>> tablesOpt = null;
			try {
				tablesOpt = Optional.ofNullable(DatabaseContext.getAllTablesBySchema(newVal));
			} catch (SQLException e) {
				log.error("find schemas failed", e);
				ContextManager.showAlert(e.getLocalizedMessage());
			}
			tablesOpt.ifPresent(tables -> {
				for (String tableName : tables) {
					this.tableList.getItems().add(getTablePane(tableName));
				}
			});
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
			Task<String> getTableDescTask = new Task<String>() {
				@Override
				protected String call() throws Exception {
					String tableDesc = DatabaseContext.getTableDesc(tableName);
					return tableDesc;
				}
			};
			TaskExecuter.executeTask(getTableDescTask);
			try {
				String tableDesc = getTableDescTask.get();
				this.currentTableModel.setDesc(tableDesc);
			} catch (InterruptedException | ExecutionException e) {
				log.error("ext=" + e.getLocalizedMessage(), e);
				ContextManager.showAlert(e.getLocalizedMessage());
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
			this.makeLoading(this.connBtn, "Connecting...");
			Service<TaskResult<List<String>>> connectionService = new Service<TaskResult<List<String>>>() {
				@Override
				protected Task<TaskResult<List<String>>> createTask() {
					return new Task<TaskResult<List<String>>>() {
						@Override
						protected TaskResult<List<String>> call() throws Exception {
							try {
								List<String> allSchemas = DatabaseContext.getAllSchemas();
								return TaskResult.makeSuccess(allSchemas, "成功");
							} catch (Exception e) {
								log.error("ext=" + e.getLocalizedMessage(), e);
								return TaskResult.makeException(e);
							}
						}
					};
				}
			};
			connectionService.start();
			connectionService.setOnSucceeded((value) -> {
				this.finishLoading(this.connBtn, "Connection");
				TaskResult<List<String>> taskResult = connectionService.getValue();
				if (taskResult.isSuccessFlag()) {
					schemaCmb.getItems().clear();
					schemaCmb.getItems().addAll(taskResult.getData());
					if (schemaCmb.getItems().size() > 0) {
						schemaCmb.getSelectionModel().select(0);
					}
				} else {
					ContextManager.showAlert(taskResult.getMsg());
				}
			});

		} else {
			ContextManager.showAlert("数据库信息不完整！！！");
		}
	}

	private void makeLoading(Button btn, String text) {
		btn.setText(text);
		btn.setDisable(true);
	}

	private void finishLoading(Button btn, String text) {
		btn.setText(text);
		btn.setDisable(false);
	}

	private void initDatabaseInfo() {
		// 初始化数据库信息的时候 先销毁原来的数据库上下文
		DatabaseContext.destory();
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
		this.makeLoading(this.buildBtn, "Building...");
		TaskExecuter.startBuilder(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				MainController.this.finishLoading(MainController.this.buildBtn, "Begin Build");
			}
		});
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
			if (MainController.this.currentTableModel == null) {
				return;
			}
			MainController.this.currentTableModel.setBuildFlag(newValue);
		}
	}
}
