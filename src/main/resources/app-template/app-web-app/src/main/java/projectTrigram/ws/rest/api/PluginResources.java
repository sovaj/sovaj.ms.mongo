#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.app.ws.rest.api;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import ${package}.domain.Plugin;
import ${package}.service.impl.PluginService;
import ${package}.web.app.ws.rest.exception.BusinessWebException;
import ${package}.web.app.ws.rest.exception.TechnicalWebException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST Web Service
 *
 * @author Mickael Dubois
 */
public class PluginResources {

    private static final Integer COMPONENT_CODE = 11000;
    private static final String COMPONENT_NAME = "CONFIGURATION";

    @Autowired
    private PluginService pluginService;

    /**
     * Creates a new instance of GenericResource
     */
    public PluginResources() {
    }

    @GET
    @Produces("application/json")
    public List<Plugin> getPlugins() {
        try {
            return pluginService.listPlugins();
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }

    @GET
    @Path("/{pluginId}")
    @Produces("application/json")
    public Plugin getPlugin(@Valid @NotNull @PathParam("pluginId") final String pluginId) {
        try {
            return pluginService.getPluginById(pluginId);
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }
}
