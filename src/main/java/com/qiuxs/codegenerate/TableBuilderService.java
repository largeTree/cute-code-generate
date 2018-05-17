package com.qiuxs.codegenerate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.qiuxs.codegenerate.context.CodeTemplateContext;
import com.qiuxs.codegenerate.context.ContextManager;
import com.qiuxs.codegenerate.context.DatabaseContext;
import com.qiuxs.codegenerate.model.FieldModel;
import com.qiuxs.codegenerate.model.TableModel;
import com.qiuxs.codegenerate.utils.ComnUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TableBuilderService extends Service<Boolean> {

	private static final String COLUMNS_SQL = "SELECT COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT,COLUMN_KEY FROM information_schema.`COLUMNS` WHERE TABLE_NAME = ? AND TABLE_SCHEMA = DATABASE()";

	private Connection conn;

	private Configuration conf;

	public TableBuilderService() {
		this.conf = new Configuration(Configuration.VERSION_2_3_25);
		try {
			this.conf.setTemplateLoader(new FileTemplateLoader(new File(this.getClass().getResource("/templates").getFile())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				TableBuilderService.this.conn = DatabaseContext.getConnection(null);
				List<TableModel> tableModels = CodeTemplateContext.getAllBuildTableModels();
				tableModels.forEach(tm -> {
					List<FieldModel> fieldsByTableName = TableBuilderService.this.getFieldsByTableName(tm);
					tm.setFields(fieldsByTableName);
					String outPutPath = ContextManager.getOutPutPath();
					Writer entityOut = null;
					Writer daoOut = null;
					Writer mapperOut = null;
					Writer serviceOut = null;
					Writer controllerOut = null;
					try {
						if (tm.isEntity()) {
							entityOut = builderWriter(outPutPath, tm, "entity", "java");
							TableBuilderService.this.outPut("entity", entityOut, tm);
						}
						if (tm.isDao()) {
							daoOut = builderWriter(outPutPath, tm, "dao", "java");
							TableBuilderService.this.outPut("dao", daoOut, tm);
						}
						if (tm.isMapper()) {
							mapperOut = builderWriter(outPutPath, tm, "mapper", "xml");
							TableBuilderService.this.outPut("mapper", mapperOut, tm);
						}
						if (tm.isService()) {
							serviceOut = builderWriter(outPutPath, tm, "service", "java");
							TableBuilderService.this.outPut("service", serviceOut, tm);
						}
						if (tm.isController()) {
							controllerOut = builderWriter(outPutPath, tm, "controller", "java");
							TableBuilderService.this.outPut("controller", controllerOut, tm);
						}
						TimeUnit.SECONDS.sleep(5);
					} catch (IOException | TemplateException | InterruptedException e) {
						e.printStackTrace();
					} finally {
						close(entityOut);
						close(daoOut);
						close(serviceOut);
						close(controllerOut);
					}
				});
				return true;
			}
		};
	}

	private Writer builderWriter(String outPutPath, TableModel tm, String type, String suffix) throws IOException {
		suffix = ("entity".equals(type) ? "" : ComnUtils.firstToUpperCase(type)) + "." + suffix;
		return new FileWriter(new File(getFinalOutDir(outPutPath, tm.getPackageName(), type) + tm.getClassName() + suffix));
	}

	private String getFinalOutDir(String basePath, String packageName, String type) {
		String finalPath = basePath + File.separator + packageToPath(packageName) + type + File.separator;
		File dir = new File(finalPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return finalPath;
	}

	private void outPut(String templateName, Writer out, TableModel tm) throws IOException, TemplateException {
		Template template = conf.getTemplate(templateName + ".ftl");
		template.process(tm, out);
	}

	private String packageToPath(String packageName) {
		return packageName.replace(".", File.separator) + File.separator;
	}

	private void close(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<FieldModel> getFieldsByTableName(TableModel tm) {
		List<FieldModel> fields = new ArrayList<FieldModel>();
		try {
			PreparedStatement statement = this.conn.prepareStatement(COLUMNS_SQL);
			statement.setString(1, tm.getTableName());
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				FieldModel field = new FieldModel();
				field.setColumnName(rs.getString(1));
				field.setJavaType(rs.getString(2));
				field.setComment(rs.getString(3));
				if ("pri".equalsIgnoreCase(rs.getString(4))) {
					tm.setPkClass(field.getJavaType());
				}
				fields.add(field);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fields;
	}

}