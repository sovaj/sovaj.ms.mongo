#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author Mickael Dubois
 */
public class UserDetails extends UserMinimal {

    private String password;

    private String role;

    private boolean enabled;

    private boolean credentialsNonExpired;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private Map<String, String> preference;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Map<String, String> getPreference() {
        if (preference == null) {
            return new HashMap();
        } else {
            return Collections.unmodifiableMap(preference);
        }
    }

    public void setPreference(Map<String, String> preference) {
        if (preference != null) {
            this.preference = new HashMap(preference);
        } else {
            this.preference = null;
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority[] authorities = new GrantedAuthority[]{new SimpleGrantedAuthority(this.role)};

        return Arrays.asList(authorities);
    }

}
