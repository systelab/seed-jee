package com.systelab.seed.user;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.user.entity.UserRole;
import com.systelab.seed.user.entity.UsersPage;
import com.systelab.seed.utils.TestUtil;

import java.util.UUID;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

@TmsLink("TC0002_LoginManagement_IntegrationTest")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class UserResourceTest extends RESTResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserResourceTest.class.getName());

    //doGetUser
    private Response doGetUser(UUID id){
        return given().when().get("/users/" + id);
    }
    //doDeleteUser
    private Response doDeleteUser(UUID userCreatedId){ return given().when().delete("/users/" + userCreatedId); }
    //doCreateUser
    private Response doCreateUser(User user){ return given().body(user).when().post("/users/user"); }
    //doGetAllUsers
    //doUserLogin

    private User getUserData(String name, String surname, String login, String password, String Role) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(UserRole.valueOf(Role));
        return user;
    }

    private void checkUserData(User expected, User actual){
        TestUtil.checkObjectIsNotNull("User", actual);
        TestUtil.checkField("Name", expected.getName(), actual.getName());
        TestUtil.checkField("Surname", expected.getSurname(), actual.getSurname());
        TestUtil.checkField("Login", expected.getLogin(), actual.getLogin());
        TestUtil.checkField("Password", expected.getPassword(), actual.getPassword());
        TestUtil.checkField("Role", expected.getRole().name(), actual.getRole().name());
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
/*        User user = new User();
        user.setLogin("agoncalves");
        user.setPassword("agoncalves");
        user.setName("Antonio");
        user.setSurname("Goncalves");
        user.setRole(UserRole.ADMIN);*/
        String expectedName = "Antonio";
        String expectedSurname = "Goncalves";
        String expectedLogin = "agoncalves";
        String expectedPassword = "agoncalves";
        String expectedRole = "USER";

        User user = getUserData(expectedName, expectedSurname, expectedLogin, expectedPassword, expectedRole);
        Response response = doCreateUser(user);
        User userCreated = response.then().extract().as(User.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkUserData(user, userCreated);
    }

    @Description("Delete a User by id")
    @Test
    public void testDeleteUser() {
        User user = getUserData("TestUserName", "TestUserSurname", "testUser", "testUser", "USER");
        Response response = doCreateUser(user);
        User userCreated = response.then().assertThat().statusCode(200)
            .extract().as(User.class);
        Assertions.assertNotNull(userCreated, "User not created");
        UUID userCreatedId = userCreated.getId();

        Response responseDel = doDeleteUser(userCreatedId);
        int statusCodeDeleted = responseDel.then().extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 200, statusCodeDeleted);

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

/*    @Description("Create a user with invalid data: empty mandatory fields (name, surname, login, password)")
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
    }*/

    @Description("Get User by id")
    @Test
    public void testGetUser() {
        User user = getUserData("TestUserName2", "TestUserSurname2", "testUser2", "testUser2", "USER");
        Response response = doCreateUser(user);
        User userCreated = response.then().assertThat().statusCode(200)
            .extract().as(User.class);
        UUID userCreatedId = userCreated.getId();
        TestUtil.checkObjectIsNotNull("User ID " + userCreatedId, userCreatedId);
        Response responseGet = doGetUser(userCreatedId);
        User userRetrieved = responseGet.then().assertThat().statusCode(200)
            .extract().as(User.class);
        Assertions.assertNotNull(userRetrieved, "User not retrieved");
        checkUserData(user, userRetrieved);
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
