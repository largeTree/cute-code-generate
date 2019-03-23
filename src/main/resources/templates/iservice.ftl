package ${packageName}.service;

import ${packageName}.dao.${className}Dao;
import ${packageName}.entity.${className};
import com.qiuxs.cuteframework.core.persistent.database.service.ifc.IDataPropertyService;


/**
 * ${desc!}服务接口
 * 
 * 创建时间 ：${.now?string("yyyy-MM-dd HH:mm:ss")}
 * @author ${author!}
 */
public interface I${className}Service extends IDataPropertyService<${pkClass}, ${className}, ${className}Dao> {

}
