package ${packageName}.action;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.qiuxs.cuteframework.web.action.BaseAction;

import ${packageName}.entity.${className};
import ${packageName}.dao.${className}Dao;
import ${packageName}.service.I${className}Service;

/**
 * ${desc!}Action
 *
 * 创建时间 ：${.now?string("yyyy-MM-dd HH:mm:ss")}
 * @author ${author}
 * 
 */
 @Service
public class ${className}Action extends BaseAction<${pkClass}, ${className}, ${className}Dao, I${className}Service> {

	@Resource
	private I${className}Service ${className?lower_case}Service;

	@Override
	protected I${className}Service getService() {
		return this.${className?lower_case}Service;
	}

}
