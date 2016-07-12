#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package org.sovaj.jgl.dao.mongo.impl;

import ${package}.domain.BusinessObjectHistorized;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mickael Dubois
 */
@Repository
public class ${resourceToManage}HistorizedDAO extends MongoDAO<BusinessObjectHistorized> {

    private static final String COLLECTION = "${_resourceToManage}.history";

    @Override
    public String getCollection() {
        return COLLECTION;
    }

    @Override
    public Class<BusinessObjectHistorized> getClazz() {
        return BusinessObjectHistorized.class;
    }
}
