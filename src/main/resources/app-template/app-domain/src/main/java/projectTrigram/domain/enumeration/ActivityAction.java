#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain.enumeration;

/**
 *
 * @author Mickael Dubois
 */
public enum ActivityAction {

    CREATED, UPDATE_MAIN_INFORMATION, UPDATED_PROFIL, ADDED_FILE, ADDED_INTERVIEW, ADDED_COMMENT, UPDATE_DESCRIPTION, UPDATE_STATUS, UPDATED, PUBLISHED;
}
