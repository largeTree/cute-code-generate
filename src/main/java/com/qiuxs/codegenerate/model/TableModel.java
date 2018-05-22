package com.qiuxs.codegenerate.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableModel {

	private boolean buildFlag;
	private String author;
	private String tableName;
	private String packageName;
	private String pkClass;
	private String superClass = "com.qiuxs.cuteframework.core.persistent.entity.impl.AbstractEntity";
	private String simpleSuperClass;
	private String className;
	private String desc;
	private boolean entity = true;
	private boolean dao = true;
	private boolean mapper = true;
	private boolean service = true;
	private boolean controller = true;

	private boolean hasError = false;
	private Set<String> unkonwTypes = new HashSet<>();

	private List<FieldModel> fields;

	private Set<String> importClasses = new HashSet<>();

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
		String[] split = this.superClass.split("\\.");
		this.setSimpleSuperClass(split[split.length - 1]);
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

	public boolean isMapper() {
		return mapper;
	}

	public void setMapper(boolean mapper) {
		this.mapper = mapper;
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
			if (field.getJavaType() == null) {
				this.unkonwTypes.add(field.getColumnName());
				hasError = true;
				return;
			}
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

	public Set<String> getUnkonwTypes() {
		return unkonwTypes;
	}

	public void setUnkonwTypes(Set<String> unkonwTypes) {
		this.unkonwTypes = unkonwTypes;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	public String getSimpleSuperClass() {
		return simpleSuperClass;
	}

	public void setSimpleSuperClass(String simpleSuperClass) {
		this.simpleSuperClass = simpleSuperClass;
	}

}
