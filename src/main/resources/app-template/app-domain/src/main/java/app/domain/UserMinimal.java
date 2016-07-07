#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

import java.time.LocalDateTime;


/**
 *
 * @author Mickael Dubois
 */


public class UserMinimal extends BusinessObject {

    private String externalReferenceId;

    private String username;

    private String name;

    private String email;

    private String phoneNumber;

    private String domain;

    private String position;

    private String photoFileName;

    private LocalDateTime lastConnexionDateTime;

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDateTime getLastConnexionDateTime() {
        return lastConnexionDateTime;
    }

    public void setLastConnexionDateTime(LocalDateTime lastConnexionDateTime) {
        this.lastConnexionDateTime = lastConnexionDateTime;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

}
