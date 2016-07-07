#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.mongo.impl;

import ${package}.domain.Plugin;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mickael Dubois
 */
@Repository
public class PluginDAO extends MongoDAO<Plugin> {

    private static final String PLUGIN_COLLECTION = "plugin";

    @Override
    public String getCollection() {
        return PLUGIN_COLLECTION;
    }

    @Override
    public Class<Plugin> getClazz() {
        return Plugin.class;
    }
}
