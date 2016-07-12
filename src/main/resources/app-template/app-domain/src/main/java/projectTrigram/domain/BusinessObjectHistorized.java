#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

/**
 *
 * @author Mickael Dubois
 */
public class BusinessObjectHistorized<T> extends BusinessObject {

    private T historizedObject;
    
    public BusinessObjectHistorized(T historizedObject) {
        this.historizedObject = historizedObject;
    }
    
    public void setHistorizedObject(T historizedObject) {
        this.historizedObject = historizedObject;
    }
    
    public T setHistorizedObject() {
        return this.historizedObject;
    }

}
