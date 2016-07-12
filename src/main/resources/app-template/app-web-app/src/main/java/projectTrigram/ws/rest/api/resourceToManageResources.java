#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ws.rest.api;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import ${package}.domain.${resourceToManage};
import ${package}.service.impl.${resourceToManage}Service;
import ${package}.ws.rest.exception.BusinessWebException;
import ${package}.ws.rest.exception.TechnicalWebException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * REST Web Service
 *
 * @author Mickael Dubois
 */
public class ${resourceToManage}Resources {

    private static final Integer COMPONENT_CODE = 1000;
    private static final String COMPONENT_NAME = "${resourceToManage}";

    @Autowired
    private ${resourceToManage}Service ${_resourceToManage}Service;

    /**
     * Creates a new instance of GenericResource
     */
    public ${resourceToManage}Resources() {
    }

    @GET
    @Produces("application/json")
    public List<${resourceToManage}> get${resourceToManage}s() {
        try {
            return ${_resourceToManage}Service.lists();
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public ${resourceToManage} get${resourceToManage}(@Valid @NotNull @PathParam("id") final String pId) {
        try {
            return ${_resourceToManage}Service.getById(pId);
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }
    
    
    @PUT
    @Path("/{id}")
    @Produces("application/json")
    public ${resourceToManage} update${resourceToManage}(@Valid @NotNull @PathParam("id") final String pId,
            @Valid @NotNull final ${resourceToManage} p${resourceToManage}) {
        try {
            return ${_resourceToManage}Service.updateById(pId, p${resourceToManage});
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }
    
    
    @POST
    @Path("/")
    @Produces("application/json")
    public ${resourceToManage} add${resourceToManage}(@Valid @NotNull final ${resourceToManage} p${resourceToManage}) {
        try {
            return ${_resourceToManage}Service.add(p${resourceToManage});
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }


    
    
    @DELETE
    @Path("/{id}")
    @Produces("application/json")
    public void delete${resourceToManage}(@Valid @NotNull @PathParam("id") final String pId) {
        try {
            ${_resourceToManage}Service.deleteById(pId);
        } catch (IllegalArgumentException e) {
            throw new BusinessWebException(COMPONENT_NAME, COMPONENT_CODE, e.getMessage());
        } catch (Exception e) {
            throw new TechnicalWebException(COMPONENT_NAME, -1, e.getCause().getMessage());
        }
    }
}
