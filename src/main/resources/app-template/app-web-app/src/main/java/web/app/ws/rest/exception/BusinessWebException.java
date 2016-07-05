#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.app.ws.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This exception is to be thrown when trying to access a business method and an
 * business logic is not respected.
 *
 * @author Mickael Dubois
 */
public class BusinessWebException extends WebApplicationException {

    private static final String ERROR_TEMPLATE = "{${symbol_escape}"component${symbol_escape}":  ${symbol_escape}"%1s${symbol_escape}", ${symbol_escape}"errorCode${symbol_escape}" : %1s, ${symbol_escape}"errorDescription${symbol_escape}" :  ${symbol_escape}"%1s${symbol_escape}"}";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Constructor.
     *
     * @param pComposant - The web service or component throwing the exception
     * @param pErrorCode - The internal/application specific error code
     * @param pErrorDescription - The error description
     *
     */
    public BusinessWebException(final String pComposant, final Integer pErrorCode, final String pErrorDescription) {
        this(String.format(ERROR_TEMPLATE, pComposant, pErrorCode, pErrorDescription));
    }

    /**
     * Constructor.
     *
     * @param pMessage The exception message to set
     */
    private BusinessWebException(final String pMessage) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(pMessage.replace("${symbol_escape}n", "").replace("${symbol_escape}r", "").replace(LINE_SEPARATOR, "")).type(MediaType.APPLICATION_JSON).build());
    }

}
