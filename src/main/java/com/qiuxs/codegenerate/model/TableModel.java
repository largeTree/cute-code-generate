package com.qiuxs.codegenerate.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TableModel {

	private boolean buildFlag;
	private String author;
	private String tableName;
	private String packageName;
	private String pkClass;
	private String superClass = "com.qiuxs.frm.persistent.entiry.AbstractEntity";
	private String className;
	private String desc;
	private boolean entity = true;
	private boolean dao = true;
	private boolean service = true;
	private boolean controller = true;

	private List<FieldModel> fields;

	private Set<String> importClasses;

	public boolean isBuildFlag() {
		return buildFlag;
	}

	public void setBuildFlag(boolean buildFlag) {
		this.buildFlag = buildFlag;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPkClass() {
		return pkClass;
	}

	public void setPkClass(String pkClass) {
		this.pkClass = pkClass;
	}

	public String getSuperClass() {
		return superClass;
	}

	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isEntity() {
		return entity;
	}

	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	public boolean isDao() {
		return dao;
	}

	public void setDao(boolean dao) {
		this.dao = dao;
	}

	public boolean isService() {
		return service;
	}

	public void setService(boolean service) {
		this.service = service;
	}

	public boolean isController() {
		return controller;
	}

	public void setController(boolean controller) {
		this.controller = controller;
	}

	public List<FieldModel> getFields() {
		return fields;
	}

	public void setFields(List<FieldModel> fields) {
		this.fields = fields;
		this.fields.forEach(field -> {
			if (Date.class.getSimpleName().equals(field.getJavaType())) {
				this.importClasses.add(Date.class.getName() + ";");
			}
			if (BigDecimal.class.getSimpleName().equals(field.getJavaType())) {
				this.importClasses.add(BigDecimal.class.getName() + ";");
			}
		});
	}

	public Set<String> getImportClasses() {
		return importClasses;
	}

	public void setImportClasses(Set<String> importClasses) {
		this.importClasses = importClasses;
	}

}
