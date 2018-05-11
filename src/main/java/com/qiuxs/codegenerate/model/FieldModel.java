package com.qiuxs.codegenerate.model;

import com.qiuxs.codegenerate.utils.ComnUtils;

public class FieldModel {

	private String columnName;
	private String name;
	private String javaType;
	private String comment;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
		this.setName(ComnUtils.formatName(columnName));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String dbType) {
		this.javaType = getJavaType(dbType);
	}

	public String getComment() {
		if (ComnUtils.isBlank(comment)) {
			this.comment = this.getName();
		}
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	private String getJavaType(String dbType) {
		String javaType = null;
		switch (dbType) {
		case "int":
		case "tinyint":
			javaType = "Integer";
			break;
		case "bigint":
			javaType = "Long";
			break;
		case "varchar":
		case "json":
		case "text":
			javaType = "String";
			break;
		case "decimal":
			javaType = "BigDecimal";
			break;
		case "date":
		case "datetime":
			javaType = "Date";
			break;
		}
		return javaType;
	}

}
