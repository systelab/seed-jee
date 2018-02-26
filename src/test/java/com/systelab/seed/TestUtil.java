package com.systelab.seed;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class TestUtil {

    @Step("Check that the returning value is true")
    public static void checkResultIsTrue(boolean b) {
        Assertions.assertTrue(b);
    }

    @Step("Check that the {0} is equals to {1}")
    public static void checkField(String field, String value, String returnedValue) {
        Assertions.assertEquals(value, returnedValue);
    }

    @Step("Check that the {0} is not null")
    public static void checkObjectIsNotNull(String objectType, Object object) {
        Assertions.assertNotNull(object);
    }

    @Step("Check that the {0} is null")
    public static void checkObjectIsNull(String objectType, Object object) {
        Assertions.assertNull(object);
    }

    @Step("Check that the error code is {0}")
    public static void checkThatIHaveAnExceptionException(int expectedCode, int code) {
        Assertions.assertEquals(expectedCode, code);
    }

    @Step("{0} {1}")
    public static void checkANumber(String message, int expected, int value) {
        Assertions.assertEquals(expected, value);
    }

    @Step("The returned id is {1}")
    public static void printReturnedId(String idName, long id) {
    }
}
