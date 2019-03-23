package ${packageName}.service.impl;

import java.util.List;
import javax.annotation.Resource;
<#if importClasses??>
<#list importClasses as importClass>
import ${importClass}
</#list>
</#if>

import org.springframework.stereotype.Service;

import com.qiuxs.cuteframework.core.persistent.database.modal.PropertyWrapper;
import com.qiuxs.cuteframework.core.persistent.database.modal.BaseField;
import com.qiuxs.cuteframework.core.persistent.database.service.AbstractDataPropertyService;
import com.qiuxs.cuteframework.core.persistent.database.service.filter.IServiceFilter;
import com.qiuxs.cuteframework.core.persistent.database.service.filter.impl.IdGenerateFilter;
import ${packageName}.dao.${className}Dao;
import ${packageName}.entity.${className};
import ${packageName}.service.I${className}Service;
/**
 * ${desc!}服务类
 *
 * @author ${author}
 *
 */
@Service
public class ${className}Service extends AbstractDataPropertyService<${pkClass}, ${className}, ${className}Dao> implements I${className}Service {

	private static final String TABLE_NAME = "${tableName}";

	public ${className}Service() {
		super(${pkClass}.class, ${className}.class, TABLE_NAME);
	}

	@Resource
	private ${className}Dao ${className?uncap_first}Dao;

	@Override
	protected ${className}Dao getDao() {
		return this.${className?uncap_first}Dao;
	}

	@Override
	protected void initServiceFilters(List<IServiceFilter<${pkClass}, ${className}>> serviceFilters) {
		serviceFilters.add(new IdGenerateFilter<>(TABLE_NAME));
	}

	@Override
	protected void initProps(List<PropertyWrapper<?>> props) {
		super.initProps(props);
		
		PropertyWrapper<?> prop = null;

		<#list fields as field>
		<#if !field.ignoreEntity>
		
		prop = new PropertyWrapper<${field.javaType}>(new BaseField("${field.name}", "${field.comment!field.name}", ${field.javaType}.class), null);
		props.add(prop);
		</#if>
		</#list>
	}

}
