package ${packageName}.entity;

<#if importClasses??>
<#list importClasses as importClass>
import ${importClass}
</#list>
</#if>

import ${superClass};

/**
 * ${desc!}实体类
 *	for table ${tableName}
 * @author ${author!}
 *
 */

public class ${className} extends ${simpleSuperClass}<${pkClass}> {

	<#list fields as field>
	<#if !field.ignoreEntity>
	/** ${field.comment!} */
	private ${field.javaType} ${field.name};

	</#if>
	</#list>

	<#list fields as field>
	<#if !field.ignoreEntity>
	/**
	 * get the ${field.comment!field.name}
	 * @return ${field.name}
	 */
	public ${field.javaType} <#if field.javaType == 'Boolean'>is<#else>get</#if>${field.name?cap_first}() {
		return this.${field.name};
	}

	/**
	 * set the ${field.comment!field.name}
	 * @param ${field.name}
	 */
	public void set${field.name?cap_first}(${field.javaType} ${field.name}) {
		this.${field.name} = ${field.name};
	}

	</#if>
	</#list>
}