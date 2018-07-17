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

@TmsLink("TC0002_LoginManagement_IntegrationTest")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
@DisplayName("User Test Suite")
public class UserClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(UserClientTest.class.getName());

    public static UserClient clientForUser;

    @BeforeAll
    public static void init() throws RequestException {
        clientForUser = new UserClient();
        login(clientForUser);
    }

    @DisplayName("Get the User list")
    @Description("Action: Get a list of users, and check that are all the users of the DB")
    @Tag("user")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetUserList() throws RequestException {
        login(clientForUser);

        List<User> users = clientForUser.get();
        clientForUser.get().stream().forEach((user)->logger.info(user.getSurname()));
        Assertions.assertNotNull(users);
    }

    @DisplayName("Create a User.")
    @Description("Action: Create a user with name, login and password and check that the values are stored.")
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
