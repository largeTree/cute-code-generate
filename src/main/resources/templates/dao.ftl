package ${packageName}.dao;

import org.springframework.stereotype.Repository;

import com.qiuxs.cuteframework.core.persistent.database.dao.IBaseDao;
import ${packageName}.entity.${className};

/**
 * ${desc!}Dao接口
 * 
 * 创建时间 ：${.now?string("yyyy-MM-dd HH:mm:ss")}
 * @author ${author}
 *
 */
@Repository
public interface ${className}Dao extends IBaseDao<${pkClass}, ${className}> {

}
