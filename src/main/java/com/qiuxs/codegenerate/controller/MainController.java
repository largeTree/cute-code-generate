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
import javafx.collections.ObservableList;
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
import javafx.scene.control.TitledPane;
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
	private CheckBox selectAllCkBox;
	@FXML
	private TitledPane tablePane;
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
	private TextField prefix;
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
	private CheckBox actionCkBox;

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
				tablePane.setText(tablePane.getText() + "(" + tables.size() + ")");
				for (String tableName : tables) {
					this.tableList.getItems().add(getTablePane(tableName));
				}
			});
		});

		// 选择表
		this.tableList.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
			MainController.this.selectTableEvent(newVal, false);
		});

		// 全选按钮
		this.selectAllCkBox.selectedProperty().addListener((ovservable, oldVal, newVal) -> {
			ObservableList<Pane> items = this.tableList.getItems();
			if (newVal) {
				String currentTableName = null;
				if (this.currentTableModel != null) {
					currentTableName = this.currentTableModel.getTableName();
				}
				Pane currentSelectedPane = null;
				// 全选
				if (items.size() > 0) {
					for (Pane pane : items) {
						this.selectTableEvent(pane, true);
						if (pane.getUserData().equals(currentTableName)) {
							currentSelectedPane = pane;
						}
					}
				}
				// 重新选中之前按选的表
				if (currentSelectedPane != null) {
					this.selectTableEvent(currentSelectedPane, true);
				}
			} else {
				// 全不选
				items.forEach(pane -> {
					// 获取表名
					String tableName = pane.getUserData().toString();
					// 获取表模型
					TableModel tableModel = CodeTemplateContext.getOrCreateTableModel(tableName);
					tableModel.setBuildFlag(false);
					CheckBox ckBox = (CheckBox) pane.getChildren().get(0);
					ckBox.setSelected(false);
				});
			}
		});

		this.tableControls.add(this.packageName);
		this.tableControls.add(this.superClass);
		this.tableControls.add(this.className);
		this.tableControls.add(this.desc);
		this.tableControls.add(this.entityCkBox);
		this.tableControls.add(this.daoCkBox);
		this.tableControls.add(this.mapperCkBox);
		this.tableControls.add(this.serviceCkBox);
		this.tableControls.add(this.actionCkBox);

		// 默认所有控件禁用，选择表并勾选build后启用
		this.setDisableFlag(true);

		// 默认输出路径
		this.outPutPathInput.setText(System.getProperty("user.home") + "\\Desktop");
		ContextManager.setOutPutPath(this.outPutPathInput.getText());
		// 默认作者
		this.author.setText(System.getProperty("user.name"));
		// 包名
		this.packageName.setText("com." + this.author.getText() + ".biz");
		// 初始化数据库信息
		initDatabaseInfo();
	}

	private void selectTableEvent(Pane pane, boolean forceSelect) {
		// 将控件的值刷新到模型中
		this.refreshTableModel();
		// 获取表名
		String tableName = pane.getUserData().toString();
		// 获取表模型
		TableModel tableModel = CodeTemplateContext.getOrCreateTableModel(tableName);
		// 设置为当前表模型
		this.currentTableModel = tableModel;
		CheckBox ckBox = (CheckBox) pane.getChildren().get(0);
		// 第一次单击时 如果没有勾选则自动勾选一下
		if (forceSelect || ckBox.getUserData() == null && !ckBox.isSelected()) {
			ckBox.setSelected(true);
			// 设置一个userData，认为已经自动勾选过
			ckBox.setUserData(new Object());
		}
		// 设置当前表是否需要构建
		this.currentTableModel.setBuildFlag(ckBox.isSelected());
		// 设置一下前缀
		if (ComnUtils.isBlank(this.currentTableModel.getPrefix())) {
			this.currentTableModel.setPrefix(this.prefix.getText());
		}
		// 还未设置过类名的情况下，自动生成一个类名
		if (ComnUtils.isBlank(this.currentTableModel.getClassName())) {
			this.currentTableModel.setClassName(ComnUtils.firstToUpperCase(ComnUtils.formatName(tableName, this.currentTableModel.getPrefix())));
		}
		// 表描述信息为空时，自动获取一下数据库的备注
		if (ComnUtils.isBlank(tableModel.getDesc())) {
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
		}
		// 刷新控件
		this.refreshControl();
	}

	private Pane getTablePane(String tableName) {
		// 选择框
		CheckBox tbCk = new CheckBox();
		tbCk.setText("");
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
		String packName = this.currentTableModel.getPackageName();
		if (ComnUtils.isNotBlank(packName)) {
			this.packageName.setText(packName);
		}
		String sPrefix = this.currentTableModel.getPrefix();
		if (ComnUtils.isNotBlank(sPrefix)) {
			this.prefix.setText(sPrefix);
		}
		this.setDisableFlag(!this.currentTableModel.isBuildFlag());
		this.superClass.setText(this.currentTableModel.getSuperClass());
		this.className.setText(this.currentTableModel.getClassName());
		this.desc.setText(this.currentTableModel.getDesc());
		this.entityCkBox.setSelected(this.currentTableModel.isEntity());
		this.daoCkBox.setSelected(this.currentTableModel.isDao());
		this.mapperCkBox.setSelected(this.currentTableModel.isMapper());
		this.serviceCkBox.setSelected(this.currentTableModel.isService());
		this.actionCkBox.setSelected(this.currentTableModel.isAction());
	}

	private void refreshTableModel() {
		if (this.currentTableModel == null) {
			return;
		}
		this.currentTableModel.setAuthor(this.author.getText());
		this.currentTableModel.setPackageName(this.packageName.getText());
		this.currentTableModel.setPrefix(this.prefix.getText());
		this.currentTableModel.setSuperClass(this.superClass.getText());
		this.currentTableModel.setClassName(this.className.getText());
		this.currentTableModel.setDesc(this.desc.getText());
		this.currentTableModel.setEntity(this.entityCkBox.isSelected());
		this.currentTableModel.setDao(this.daoCkBox.isSelected());
		this.currentTableModel.setMapper(this.mapperCkBox.isSelected());
		this.currentTableModel.setService(this.serviceCkBox.isSelected());
		this.currentTableModel.setAction(this.actionCkBox.isSelected());
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
