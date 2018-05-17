package ${packageName}.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.qiuxs.frm.persistent.service.AbstractDataService;
import ${packageName}.dao.UserDao;
import ${packageName}.entity.User;

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
	private ${className}Dao ${className?lower_case}Dao;

	@Override
	protected ${className}Dao getDao() {
		return this.${className?lower_case}Dao;
	}

}
