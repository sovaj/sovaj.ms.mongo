#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.mongo.impl;

import ${package}.domain.${resourceToManage};
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mickael Dubois
 */
@Repository
public class ${resourceToManage}DAO extends MongoDAO<${resourceToManage}> {

    private static final String COLLECTION = "${_resourceToManage}";

    @Override
    public String getCollection() {
        return COLLECTION;
    }

    @Override
    public Class<${resourceToManage}> getClazz() {
        return ${resourceToManage}.class;
    }
}
