package com.systelab.seed.user;

import com.systelab.seed.RESTResourceTest;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.entity.Patient;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.user.entity.UserRole;
import com.systelab.seed.user.entity.UsersPage;
import com.systelab.seed.utils.FakeNameGenerator;
import com.systelab.seed.utils.TestUtil;

import java.util.List;
import java.util.UUID;

import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import static java.util.stream.Collectors.joining;

@TmsLink("TC0002_LoginManagement_IntegrationTest")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
class UserResourceTest extends RESTResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserResourceTest.class.getName());


    private Response doGetUser(UUID id){
        return given().when().get("/users/" + id);
    }

    private Response doDeleteUser(UUID userCreatedId){ return given().when().delete("/users/" + userCreatedId); }

    private Response doCreateUser(User user){ return given().body(user).when().post("/users/user"); }

    private Response doGetUserList(){
        return given().when().get("/users");
    }

    private Response doUserLogin(String login, String password){
        return given().contentType("application/x-www-form-urlencoded").formParam("login", login).formParam("password", password).
            when().post("/users/login");
    }

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
        TestUtil.checkField("Role", expected.getRole().name(), actual.getRole().name());
    }

    @Attachment(value = "Users Database")
    private String saveUsersDatabase(List<User> users) {
        return users.stream().map((user) -> user.getName() + "\t" + user.getSurname() + "\t" + user.getLogin()).collect(joining("\n"));
    }

    @Step("Action: Create {0} users")
    private void createSomeUsers(int numberOfUsers) {
        FakeNameGenerator aFakeNameGenerator = new FakeNameGenerator();
        for (int i = 0; i < numberOfUsers; i++) {
            User user = getUserData(aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true), aFakeNameGenerator.generateName(true),aFakeNameGenerator.generateName(true),"USER");
            Response response = doCreateUser(user);
            User userCreated = response.then().assertThat().statusCode(200)
                .extract().as(User.class);
            Assertions.assertNotNull(userCreated.getId(), "User not created");
        }
    }

    @Description("Get the User list")
    @Test
    void testGetUserList() {
        int numberOfUsers = 1;
        Response response = doGetUserList();
        UsersPage users = response.then().assertThat().statusCode(200)
                .extract().as(UsersPage.class);

        users.getContent().stream().forEach((user) -> logger.info(user.getSurname()));
        TestUtil.checkObjectIsNotNull("Users", users);

        long initialSize = users.getTotalElements();
        createSomeUsers(numberOfUsers);

        Response responseAfter = doGetUserList();
        UsersPage usersFinal = responseAfter.then().assertThat().statusCode(200)
            .extract().as(UsersPage.class);
        long finalSize = usersFinal.getTotalElements();
        TestUtil.checkANumber("List size", initialSize + numberOfUsers, finalSize);
    }

    @Description("Create a User with name, login and password")
    @Test
    void testCreateUser() {
        String expectedName = "test3";
        String expectedSurname = "test3";
        String expectedLogin = "test3";
        String expectedPassword = "test3";
        String expectedRole = "USER";

        User user = getUserData(expectedName, expectedSurname, expectedLogin, expectedPassword, expectedRole);
        Response response = doCreateUser(user);
        User userCreated = response.then().assertThat().statusCode(200).extract().as(User.class);
        int status = response.then().extract().statusCode();
        TestUtil.checkField("Status Code", 200, status);
        checkUserData(user, userCreated);

        UUID userCreatedId = userCreated.getId();
        Response responseDel = doDeleteUser(userCreatedId);
        int statusCodeDeleted = responseDel.then().extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 200, statusCodeDeleted);
    }

    @Description("Delete a User by id")
    @Test
    void testDeleteUser() {
        String expectedName = "TestUserName2";
        String expectedSurname = "TestUserSurname2";
        String expectedLogin = "testUser2";
        String expectedPassword = "testUser2";
        String expectedRole = "USER";

        User user = getUserData(expectedName, expectedSurname, expectedLogin, expectedPassword, expectedRole);
        Response response = doCreateUser(user);
        User userCreated = response.then().assertThat().statusCode(200).extract().as(User.class);
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

    @Description("Create a user with invalid data: empty mandatory fields (name, surname, login, password)")
    @Test
    void testCreateInvalidUserEmptyMandatoryFields() {
        testCreateInvalidUser(getUserData("", "Jones", "jjones", "passJJones", "USER"));
        testCreateInvalidUser(getUserData("Jude", "", "jjones", "passJJones", "USER"));
        testCreateInvalidUser(getUserData("Jude", "Jones", "", "passJJones", "USER"));
        testCreateInvalidUser(getUserData("Jude", "Jones", "jjones", "", "USER"));
    }

    @Description("Create a user with invalid data: text fields too long (name, surname, login, password)")
    @Test
    void testCreateInvalidUserTooLongText() {
        String tooLongString = "thisStringIsIntendedToCauseAnExceptionBecauseOfItsExcessiveLengthTheMostLongStringAllowedMustHaveLessThanTeoHundredAndFiftyFiveCharactersThisShouldBeVerifiedInEveryTextFieldToEnsureTheLimitationIsWorkingProperlyThisStringOnlyHasEnglishLettersButMoreScenarios";
        String tooLongLogin = "12345678901";

        testCreateInvalidUser(getUserData(tooLongString, "Jones", "jjones", "passJJones", "USER"));
        testCreateInvalidUser(getUserData("Jude", tooLongString, "jjones", "passJJones", "USER"));
        testCreateInvalidUser(getUserData("Jude", "Jones", tooLongLogin, "passJJones", "USER"));
        testCreateInvalidUser(getUserData("Jude", "Jones", "jjones", tooLongString, "USER"));
    }

    @Description("Get User by id")
    @Test
    void testGetUser() {
        String expectedName = "GetUserName2";
        String expectedSurname = "GetUserSurname2";
        String expectedLogin = "GetUser2";
        String expectedPassword = "GetUser2";
        String expectedRole = "USER";
        User user = getUserData(expectedName, expectedSurname, expectedLogin, expectedPassword, expectedRole);

        Response response = doCreateUser(user);
        User userCreated = response.then().assertThat().statusCode(200).extract().as(User.class);
        UUID userCreatedId = userCreated.getId();
        TestUtil.checkObjectIsNotNull("User ID " + userCreatedId, userCreatedId);
        Response responseGet = doGetUser(userCreatedId);
        User userRetrieved = responseGet.then().assertThat().statusCode(200)
            .extract().as(User.class);
        Assertions.assertNotNull(userRetrieved, "User not retrieved");
        checkUserData(user, userRetrieved);

        Response responseDel = doDeleteUser(userCreatedId);
        int statusCodeDeleted = responseDel.then().extract().statusCode();
        TestUtil.checkField("Status Code after a GET", 200, statusCodeDeleted);
    }

    @Description("Login - Successful")
    @Test
    void testLoginOK() {
        String login = "Systelab";
        String password = "Systelab";
        Response response = doUserLogin(login, password);
        String auth = response.getHeader(AUTHORIZATION_HEADER);
        TestUtil.checkObjectIsNotNull("Auth. ", auth);
    }

    @Description("Login - Unsuccessful")
    @Test
    void testLoginKO() {
        String login = "fakeUser";
        String password = "noPass";
        Response response = doUserLogin(login, password);
        String auth = response.getHeader(AUTHORIZATION_HEADER);
        TestUtil.checkObjectIsNull("Auth. ", auth);
    }
}
