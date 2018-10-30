package com.systelab.seed.user.boundary.bean;

import com.systelab.seed.user.entity.User;
import com.systelab.seed.user.boundary.UserService;
import com.systelab.seed.BaseException;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;
import com.systelab.seed.infrastructure.auth.AuthenticationTokenGenerator;
import com.systelab.seed.infrastructure.security.PasswordDigest;
import com.systelab.seed.user.boundary.UserNotFoundException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
public class UserServiceBean implements UserService {
    @PersistenceContext(unitName = "SEED")
    private EntityManager em;

    private AuthenticationTokenGenerator tokenGenerator;
    private PasswordDigest passwordDigest;

    @Inject
    public void setAuthenticationTokenGenerator(AuthenticationTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Inject
    public void setPasswordDigest(PasswordDigest passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    @Override
    public User getUser(UUID id) {
        return em.find(User.class, id);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        TypedQuery<Long> queryTotal = em.createNamedQuery(User.ALL_COUNT, Long.class);
        TypedQuery<User> query = em.createNamedQuery(User.FIND_ALL, User.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<User> users = query.getResultList();
        long totalElements = queryTotal.getSingleResult();

        return new Page<>(users, totalElements);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(User user) throws BaseException {
        user.setPassword(passwordDigest.digest(user.getPassword()));
        em.persist(user);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(UUID id) throws UserNotFoundException {
        User u = em.find(User.class, id);
        if (u != null) {
            em.remove(u);
        } else {
            throw new UserNotFoundException();
        }
    }

    @Override
    public String getToken(String uri, String login, String password) throws BaseException {
        User user = authenticate(login, password);
        return tokenGenerator.issueToken(user.getLogin(), user.getRole().toString(), uri);

    }

    private User authenticate(String login, String password) throws BaseException {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("login", login);
        query.setParameter("password", passwordDigest.digest(password));
        User user = query.getSingleResult();

        if (user == null)
            throw new SecurityException("Invalid user/password");

        return user;
    }

}
