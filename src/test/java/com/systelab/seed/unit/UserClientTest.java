package com.systelab.seed.unit;

import com.systelab.seed.TestUtil;
import com.systelab.seed.client.RequestException;
import com.systelab.seed.client.UserClient;
import com.systelab.seed.model.user.User;

import java.util.List;
import java.util.logging.Logger;

import com.systelab.seed.model.user.UserRole;
import org.junit.jupiter.api.*;
import io.qameta.allure.*;


@Feature("User Test Suite")
@DisplayName("User Test Suite")
public class UserClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(UserClientTest.class.getName());

    public static UserClient clientForUser;

    @BeforeAll
    public static void init() throws RequestException {
        clientForUser = new UserClient();
        login(clientForUser);
    }

    @TmsLink("SEED-USR-1")
    @DisplayName("Test User list")
    @Description("Test that we can get a List of users.")
    @Tag("user")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetUserList() throws RequestException {
        login(clientForUser);

        List<User> users = clientForUser.get();

        for (int i = 0; i < users.size(); i++) {
            logger.info(users.get(i).getSurname());
        }

        Assertions.assertNotNull(users);
    }

    @TmsLink("SEED-USR-2")
    @Link(value = "REQ-USR-2", type = "requirement")
    @DisplayName("Test create a User.")
    @Description("Test that is possible to create a user.\n\nPrerequisites:\n\n" + "- Prerequisite 1\n" + "- Prerequisite 2\n" + "- Prerequisite 3\n")
    @Tag("user")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testCreateUser() throws RequestException {
        User user =new User();
        user.setLogin("agoncalves");
        user.setPassword("agoncalves");
        user.setName("Antonio");
        user.setSurname("Goncalves");
        user.setRole(UserRole.ADMIN);

        User userCreated = clientForUser.create(user);
        TestUtil.checkObjectIsNotNull("user", userCreated);
        TestUtil.checkField("Name", "Antonio", userCreated.getName());
        TestUtil.checkField("Surname", "Goncalves", userCreated.getSurname());
    }
}
