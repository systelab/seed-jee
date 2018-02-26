package com.systelab.seed.unit;

import com.systelab.seed.client.RequestException;
import com.systelab.seed.client.UserClient;
import com.systelab.seed.model.user.User;

import java.util.List;
import java.util.logging.Logger;

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

}
