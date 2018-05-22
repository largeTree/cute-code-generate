package ${packageName}.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qiuxs.cuteframework.web.WebConstants;
import com.qiuxs.cuteframework.web.controller.AbstractDataController;

import ${packageName}.dao.${className}Dao;
import ${packageName}.entity.${className};
import ${packageName}.service.${className}Service;

/**
 * ${desc!}控制器
 *
 * @author ${author}
 *
 */
@RestController
@RequestMapping(value = "/api/${className?lower_case}", produces = WebConstants.DEFAULT_REQUEST_PRODUCES)
public class ${className}Controller extends AbstractDataController<${pkClass}, ${className}, ${className}Dao, ${className}Service> {

	@Resource
	private ${className}Service ${className?lower_case}Service;

	@Override
	protected ${className}Service getService() {
		return this.${className?lower_case}Service;
	}

}
