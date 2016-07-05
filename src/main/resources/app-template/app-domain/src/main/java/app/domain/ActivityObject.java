#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.domain;

/**
 *
 * @author Mickael Dubois
 */
public class ActivityObject extends BusinessObject {

    private Long jobNum;

    private String title;

    private String username;

    private String name;

    public Long getJobNum() {
        return jobNum;
    }

    public void setJobNum(Long jobNum) {
        this.jobNum = jobNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

}
