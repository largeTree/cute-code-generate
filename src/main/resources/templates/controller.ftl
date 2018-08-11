package ${packageName}.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qiuxs.cuteframework.web.WebConstants;
import com.qiuxs.cuteframework.web.controller.AbstractDataController;

import ${packageName}.dao.${className}Dao;
import ${packageName}.entity.${className};
import ${packageName}.service.I${className}Service;

/**
 * ${desc!}控制器
 *
 * @author ${author}
 *
 */
@RestController
@RequestMapping(value = WebConstants.DEFAULT_API_PREFIX + "/${className?lower_case}", produces = WebConstants.DEFAULT_REQUEST_PRODUCES)
public class ${className}Controller extends AbstractDataController<${pkClass}, ${className}, ${className}Dao, I${className}Service> {

	@Resource
	private I${className}Service ${className?lower_case}Service;

	@Override
	protected I${className}Service getService() {
		return this.${className?lower_case}Service;
	}

}
