package ${packageName}.service;

import java.util.List;
import javax.annotation.Resource;
<#if importClasses??>
<#list importClasses as importClass>
import ${importClass}
</#list>
</#if>

import org.springframework.stereotype.Service;

import com.qiuxs.cuteframework.core.persistent.modal.PropertyWrapper;
import com.qiuxs.cuteframework.core.persistent.modal.BaseField;
import com.qiuxs.cuteframework.core.persistent.service.AbstractDataService;
import ${packageName}.dao.${className}Dao;
import ${packageName}.entity.${className};

/**
 * ${desc!}服务类
 *
 * @author ${author}
 *
 */
@Service
public class ${className}Service extends AbstractDataService<${pkClass}, ${className}, ${className}Dao> {

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
		PropertyWrapper<?> prop = null;

		<#list fields as field>
		prop = new PropertyWrapper<${field.javaType}>(new BaseField("${field.name}", "${field.comment!field.name}", "${field.javaType}"), null);
		props.add(prop);

		</#list>
	}

}
