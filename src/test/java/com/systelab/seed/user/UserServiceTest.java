package com.systelab.seed.user;

import com.systelab.seed.BaseEntity;
import com.systelab.seed.BaseException;
import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.infrastructure.SLF4JProducer;
import com.systelab.seed.infrastructure.auth.AuthenticationTokenGenerator;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;
import com.systelab.seed.infrastructure.security.PasswordDigest;
import com.systelab.seed.user.boundary.UserService;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.user.entity.UserRole;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class UserServiceTest {

    @Inject
    private UserService userService;

    @Deployment
    public static WebArchive createDeployment() {

        // Create deploy file
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(SLF4JProducer.class, BaseException.class, BaseEntity.class, AuthenticationTokenGenerator.class, PasswordDigest.class, Pageable.class, Page.class,
                        Logger.class, RESTResourceTest.class)
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsResource("META-INF/beans.xml")
                .addPackages(true, "com.systelab.seed.user", "org.apache.poi", "com.systelab.seed.infrastructure.security", "io.jsonwebtoken", "org.slf4j", "com.systelab.seed.infrastructure.auth");
    }

    @Test
    public void createUser() throws BaseException {
        User user = new User();
        user.setLogin("agoncalves");
        user.setPassword("agoncalves");
        user.setName("Antonio");
        user.setSurname("Goncalves");
        user.setRole(UserRole.ADMIN);

        userService.create(user);

        assertNotNull(userService.getUser(user.getId()));
    }

}
