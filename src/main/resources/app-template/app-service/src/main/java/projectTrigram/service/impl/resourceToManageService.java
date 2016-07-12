#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.impl;

import java.util.List;
import ${package}.domain.BusinessObjectHistorized;
import ${package}.domain.${resourceToManage};
import ${package}.dao.mongo.impl.${resourceToManage}DAO;
import ${package}.dao.mongo.impl.${resourceToManage}HistorizedDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mickael Dubois
 */
@Service
public class ${resourceToManage}Service {

    @Autowired
    private ${resourceToManage}DAO ${_resourceToManage}DAO;

    @Autowired
    private ${resourceToManage}HistorizedDAO ${_resourceToManage}HistorizedDAO;

    public List<${resourceToManage}> lists() {
        return ${_resourceToManage}DAO.list();
    }

    public ${resourceToManage} getById(final String pId) {
        return ${_resourceToManage}DAO.getById(pId);
    }

    public ${resourceToManage} add(final ${resourceToManage} p${resourceToManage}) {
        p${resourceToManage}.setVersion(1L);
        ${_resourceToManage}DAO.add(p${resourceToManage});
        return p${resourceToManage};
    }

    public ${resourceToManage} updateById(final String pId, final ${resourceToManage} p${resourceToManage}) {
        ${resourceToManage} oldVersion = this.getById(pId);
        p${resourceToManage}.setId(pId);
        p${resourceToManage}.setVersion(oldVersion.getVersion() + 1);
        ${_resourceToManage}HistorizedDAO.add(new BusinessObjectHistorized(oldVersion));
        ${_resourceToManage}DAO.update(p${resourceToManage});
        return p${resourceToManage};
    }

    public void deleteById(final String pId) {
        ${_resourceToManage}DAO.deleteById(pId);
    }
}
