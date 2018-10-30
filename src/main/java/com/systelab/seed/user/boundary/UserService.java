package com.systelab.seed.user.boundary;

import com.systelab.seed.BaseException;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;

import javax.ejb.Local;
import java.util.UUID;

@Local
public interface UserService {

    User getUser(UUID id);

    Page<User> getAllUsers(Pageable pageable);

    void create(User user) throws BaseException;

    void delete(UUID id) throws UserNotFoundException;

    String getToken(String uri, String login, String password) throws SecurityException, BaseException;

}
