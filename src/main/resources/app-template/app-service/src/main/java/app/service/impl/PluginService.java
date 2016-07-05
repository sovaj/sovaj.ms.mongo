#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.service.impl;

import java.util.List;
import ${package}.app.domain.Plugin;
import ${package}.app.dao.mongo.impl.PluginDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mickael Dubois
 */
@Service
public class PluginService {

    @Autowired
    private PluginDAO pluginDAO;

    public List<Plugin> listPlugins() {
        return pluginDAO.list();
    }

    public Plugin getPluginById(String pluginId) {
        return pluginDAO.getById(pluginId);
    }
}
