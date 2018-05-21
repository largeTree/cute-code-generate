package ${packageName}.entity;

<#if importClasses??>
<#list importClasses as importClass>
import ${importClass};
</#list>
</#if>

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.qiuxs.cuteframework.core.persistent.entity.impl.AbstractEntity;

/**
 * ${desc!}实体类
 *	for table ${tableName}
 * @author ${author!}
 *
 */
@Entity
public class ${className} extends AbstractEntity<${pkClass}> {

	<#list fields as field>
	/** ${field.comment!} */
	private ${field.javaType} ${field.name};

	</#list>

	<#list fields as field>
	/**
	 * get the ${field.comment!field.name}
	 * @return ${field.name}
	 */
	public ${field.javaType} <#if field.javaType == 'Boolean'>is<#else>get</#if>${field.name?cap_first}() {
		return this.${field.name}
	}

	/**
	 * set the ${field.comment!field.name}
	 * @param ${field.name}
	 */
	public void set${field.name?cap_first}(${field.javaType} ${field.name}) {
		this.${field.name} = ${field.name};
	}

	</#list>

}