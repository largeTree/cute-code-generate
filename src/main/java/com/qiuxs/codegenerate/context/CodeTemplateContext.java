package com.qiuxs.codegenerate.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiuxs.codegenerate.model.TableModel;

public class CodeTemplateContext {

	private static Map<String, TableModel> tableModelHolder = new HashMap<>();

	public static TableModel getOrCreateTableModel(String tableName) {
		TableModel tableModel = tableModelHolder.get(tableName);
		if (tableModel == null) {
			tableModel = new TableModel();
			tableModel.setTableName(tableName);
			tableModelHolder.put(tableName, tableModel);
		}
		return tableModel;
	}

	public static List<TableModel> getAllBuildTableModels() {
		List<TableModel> tableModels = new ArrayList<TableModel>();
		tableModelHolder.forEach((tableName, tableModel) -> {
			if (tableModel.isBuildFlag()) {
				tableModels.add(tableModel);
			}
		});
		return tableModels;
	}

}
