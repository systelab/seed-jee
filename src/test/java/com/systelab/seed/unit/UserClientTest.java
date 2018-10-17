package com.systelab.seed.unit;

import com.systelab.seed.TestUtil;
import com.systelab.seed.model.user.User;
import com.systelab.seed.model.user.UserRole;
import io.qameta.allure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

@TmsLink("TC0002_LoginManagement_IntegrationTest")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
@DisplayName("User Test Suite")
public class UserClientTest extends FunctionalTest {
    private static final Logger logger = Logger.getLogger(UserClientTest.class.getName());

    @DisplayName("Get the User list")
    @Description("Action: Get a list of users, and check that are all the users of the DB")
    @Tag("user")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testGetUserList() {
        UsersPage users = given().contentType("application/json").header("Authorization", bearer).when().get("/users").as(UsersPage.class);
        users.getContent().stream().forEach((user) -> logger.info(user.getSurname()));
        Assertions.assertNotNull(users);
    }

    @DisplayName("Create a User.")
    @Description("Action: Create a user with name, login and password and check that the values are stored.")
    @Tag("user")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void testCreateUser() {
        User user = new User();
        user.setLogin("agoncalves");
        user.setPassword("agoncalves");
        user.setName("Antonio");
        user.setSurname("Goncalves");
        user.setRole(UserRole.ADMIN);

        User userCreated = given().contentType("application/json").header("Authorization", bearer).body(user).when().post("/users/user").as(User.class);
        TestUtil.checkObjectIsNotNull("user", userCreated);
        TestUtil.checkField("Name", "Antonio", userCreated.getName());
        TestUtil.checkField("Surname", "Goncalves", userCreated.getSurname());
    }
}
