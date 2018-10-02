package com.systelab.seed.service;

import com.systelab.seed.model.user.User;
import com.systelab.seed.util.exceptions.SeedBaseException;
import com.systelab.seed.util.exceptions.UserNotFoundException;
import com.systelab.seed.util.pagination.Page;
import com.systelab.seed.util.pagination.Pageable;

import javax.ejb.Local;
import java.util.UUID;

@Local
public interface UserService {

    User getUser(UUID id);

    Page<User> getAllUsers(Pageable pageable);

    void create(User user) throws SeedBaseException;

    void delete(UUID id) throws UserNotFoundException;

    String getToken(String uri, String login, String password) throws SecurityException, SeedBaseException;

}
