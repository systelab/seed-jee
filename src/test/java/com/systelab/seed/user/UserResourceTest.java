package com.systelab.seed.user;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.user.entity.UsersPage;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.user.entity.UserRole;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

@TmsLink("TC0002_LoginManagement_IntegrationTest")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class UserResourceTest extends RESTResourceTest {
    private static final Logger logger = Logger.getLogger(UserResourceTest.class.getName());

    @Description("Get the User list")
    @Test
    public void testGetUserList() {
        UsersPage users = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).
                when().get("/users").as(UsersPage.class);
        users.getContent().stream().forEach((user) -> logger.info(user.getSurname()));
        TestUtil.checkObjectIsNotNull("Users", users);
    }

    @Description("Create a User with name, login and password")
    @Test
    public void testCreateUser() {
        User user = new User();
        user.setLogin("agoncalves");
        user.setPassword("agoncalves");
        user.setName("Antonio");
        user.setSurname("Goncalves");
        user.setRole(UserRole.ADMIN);

        User userCreated = given().contentType(ContentType.JSON).header(AUTHORIZATION_HEADER, getBearer()).body(user).
                when().post("/users/user").as(User.class);
        TestUtil.checkObjectIsNotNull("User", userCreated);
        TestUtil.checkField("Name", "Antonio", userCreated.getName());
        TestUtil.checkField("Surname", "Goncalves", userCreated.getSurname());
    }
}
