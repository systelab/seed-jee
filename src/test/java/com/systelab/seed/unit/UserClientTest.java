package com.systelab.seed.unit;

import com.systelab.seed.client.RequestException;
import com.systelab.seed.client.UserClient;
import com.systelab.seed.model.user.User;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Severity;
import ru.yandex.qatools.allure.annotations.TestCaseId;
import ru.yandex.qatools.allure.annotations.Title;
import ru.yandex.qatools.allure.model.SeverityLevel;

@Title("User Test Suite")
public class UserClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(UserClientTest.class.getName());

    private static UserClient clientForUser;

    @BeforeClass
    public static void init() throws RequestException {
        clientForUser = new UserClient();
        login(clientForUser);
    }

    @TestCaseId("SEED-USR-1")
    @Description("Test that we can get a List of users.")
    @Features("User")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetUserList() throws RequestException {
        login(clientForUser);

        List<User> users = clientForUser.get();

        for (int i = 0; i < users.size(); i++) {
            logger.info(users.get(i).getSurname());
        }

        Assert.assertNotNull(users);
    }

}
