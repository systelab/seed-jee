package com.systelab.seed.user;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.user.entity.UserRole;
import com.systelab.seed.user.entity.UsersPage;
import com.systelab.seed.utils.TestUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.restassured.RestAssured.given;

@TmsLink("TC0002_LoginManagement_IntegrationTest")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class UserResourceTest extends RESTResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserResourceTest.class.getName());

    private User getUserData(String name, String surname, String login, String password) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(UserRole.USER);
        return user;
    }

    @Description("Get the User list")
    @Test
    public void testGetUserList() {
        UsersPage users = given()
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .extract().as(UsersPage.class);
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

        User userCreated = given().body(user)
                .when().post("/users/user")
                .then().assertThat().statusCode(200)
                .extract().as(User.class);
        TestUtil.checkObjectIsNotNull("User", userCreated);
        TestUtil.checkField("Name", "Antonio", userCreated.getName());
        TestUtil.checkField("Surname", "Goncalves", userCreated.getSurname());
    }

    @Description("Delete a User by id")
    @Test
    public void testDeleteUser() {
        User user = new User(null, "TestUserName", "TestUserSurname", "testUser", "testUser");

        User userCreated = given().body(user)
            .when().post("/users/user")
            .then().assertThat().statusCode(200)
            .extract().as(User.class);
        Assertions.assertNotNull(userCreated);
        given()
            .when().delete("/users/" + userCreated.getId())
            .then().assertThat().statusCode(200);

        int statusCode = given()
            .when().get("/users/" + userCreated.getId())
            .then()
            .extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 404, statusCode);
    }

    private void testCreateInvalidUser(User user) {
        int statusCode = given().body(user)
            .when().post("/users/user")
            .then()
            .extract().statusCode();
        TestUtil.checkField("Status Code", 400, statusCode);
    }

    @Description("Create a user with invalid data: empty mandatory fields (name, surname, login, password)")
    @Test
    public void testCreateInvalidUserEmptyMandatoryFields() {
        testCreateInvalidUser(getUserData("", "Jones", "jjones", "passJJones"));
        testCreateInvalidUser(getUserData("Jude", "", "jjones", "passJJones"));
        testCreateInvalidUser(getUserData("Jude", "Jones", "", "passJJones"));
        testCreateInvalidUser(getUserData("Jude", "Jones", "jjones", ""));
    }

    @Description("Create a user with invalid data: text fields too long (name, surname, login, password)")
    @Test
    public void testCreateInvalidUserTooLongText() {
        String tooLongString = "thisStringIsIntendedToCauseAnExceptionBecauseOfItsExcessiveLengthTheMostLongStringAllowedMustHaveLessThanTeoHundredAndFiftyFiveCharactersThisShouldBeVerifiedInEveryTextFieldToEnsureTheLimitationIsWorkingProperlyThisStringOnlyHasEnglishLettersButMoreScenarios";
        String tooLongLogin = "12345678901";

        testCreateInvalidUser(getUserData(tooLongString, "Jones", "jjones", "passJJones"));
        testCreateInvalidUser(getUserData("Jude", tooLongString, "jjones", "passJJones"));
        testCreateInvalidUser(getUserData("Jude", "Jones", tooLongLogin, "passJJones"));
        testCreateInvalidUser(getUserData("Jude", "Jones", "jjones", tooLongString));
    }

    @Description("Get User by id")
    @Test
    public void testGetUser() {
        User user = new User(null, "TestUserName2", "TestUserSurname2", "testUser2", "testUser2");
        User userCreated = given().body(user)
            .when().post("/users/user")
            .then().assertThat().statusCode(200)
            .extract().as(User.class);
        Assertions.assertNotNull(userCreated);
        User userRetrieved = given()
            .when().get("/users/" + userCreated.getId())
            .then().assertThat().statusCode(200)
            .extract().as(User.class);
        TestUtil.checkObjectIsNotNull("User ID " + userRetrieved.getId(), userRetrieved.getId());
        if (userRetrieved != null) {
            TestUtil.checkField("Name", userCreated.getName(), userRetrieved.getName());
            TestUtil.checkField("Suname", userCreated.getSurname(), userRetrieved.getSurname());
            TestUtil.checkField("Login", userCreated.getLogin(), userRetrieved.getLogin());
            TestUtil.checkField("Password", userCreated.getPassword(), userRetrieved.getPassword());
            TestUtil.checkField("Role", userCreated.getRole().toString(), userRetrieved.getRole().toString());
        }
    }

    @Description("Login - Successful")
    @Test
    public void testLoginOK() {
        String login = "Systelab";
        String password = "Systelab";
        String auth = given().contentType("application/x-www-form-urlencoded").formParam("login", login).formParam("password", password).
            when().post("/users/login").getHeader(AUTHORIZATION_HEADER);

        TestUtil.checkObjectIsNotNull("Auth. ", auth);
    }

    @Description("Login - Unsuccessful")
    @Test
    public void testLoginKO() {
        String login = "fakeUser";
        String password = "noPass";
        String auth = given().contentType("application/x-www-form-urlencoded").formParam("login", login).formParam("password", password).
            when().post("/users/login").getHeader(AUTHORIZATION_HEADER);
        TestUtil.checkObjectIsNull("Auth. ", auth);
    }
}
