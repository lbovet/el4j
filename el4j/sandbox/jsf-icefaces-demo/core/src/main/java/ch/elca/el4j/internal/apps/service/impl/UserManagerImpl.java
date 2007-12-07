package ch.elca.el4j.internal.apps.service.impl;

import org.acegisecurity.userdetails.UsernameNotFoundException;
import ch.elca.el4j.internal.apps.dao.UserDao;
import ch.elca.el4j.internal.apps.model.User;
import ch.elca.el4j.internal.apps.service.UserExistsException;
import ch.elca.el4j.internal.apps.service.UserManager;
//import ch.elca.el4j.internal.apps.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityExistsException;
//import javax.jws.WebService;
import java.util.List;


/**
 * Implementation of UserManager interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
//@WebService(serviceName = "UserService", endpointInterface = "ch.elca.el4j.internal.apps.service.UserService")
public class UserManagerImpl extends UniversalManagerImpl implements UserManager {
    private UserDao dao;

    /**
     * Set the Dao for communication with the data layer.
     * @param dao the UserDao that communicates with the database
     */
    public void setUserDao(UserDao dao) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    public User getUser(String userId) {
        return dao.get(new Long(userId));
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getUsers(User user) {
        return dao.getUsers();
    }

    /**
     * {@inheritDoc}
     */
    public User saveUser(User user) throws UserExistsException {
        // if new user, lowercase userId
        if (user.getVersion() == null) {
            user.setUsername(user.getUsername().toLowerCase());
        }

        try {
            return dao.saveUser(user);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername() + "' already exists!");
        } catch (EntityExistsException e) { // needed for JPA
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeUser(String userId) {
        log.debug("removing user: " + userId);
        dao.remove(new Long(userId));
    }

    /**
     * {@inheritDoc}
     * @param username the login name of the human
     * @return User the populated user object
     * @throws UsernameNotFoundException thrown when username not found
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return (User) dao.loadUserByUsername(username);
    }
}
