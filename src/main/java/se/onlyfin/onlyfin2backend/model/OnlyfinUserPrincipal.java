package se.onlyfin.onlyfin2backend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class is used to contain a user principal object.
 * Is used by Spring Security.
 */
public class OnlyfinUserPrincipal implements UserDetails {
    private final User user;

    public OnlyfinUserPrincipal(User user) {
        this.user = user;
    }

    /**
     * @return the username of the user.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * @return the encrypted/hashed password of the user.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * This method is used to get the roles of the user.
     *
     * @return a list of authorities that the user has.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(user
                        .getRoles()
                        .split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    /**
     * Not used.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Not used.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Not used.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Not used.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
