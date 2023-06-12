package se.onlyfin.onlyfin2backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se.onlyfin.onlyfin2backend.model.OnlyfinUserPrincipal;
import se.onlyfin.onlyfin2backend.repository.UserRepository;

/**
 * This class is responsible for loading a user from the database.
 * It is used by Spring Security.
 */
@Service
public class OnlyfinUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public OnlyfinUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is called by Spring Security when a user tries to authenticate.
     *
     * @param username the username identifying the user whose data is required.
     * @return a UserDetails object containing the user's data.
     * @throws UsernameNotFoundException if the user is not found.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .map(OnlyfinUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }

}
