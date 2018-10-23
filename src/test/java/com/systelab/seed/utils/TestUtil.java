package com.systelab.seed.utils;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class TestUtil {

    @Step("Field {0} is equal to true")
    public static void checkTrue(String field, boolean returnedValue) {
        Assertions.assertTrue(returnedValue);
    }

    @Step("Field {0} is equal to {1}")
    public static void checkField(String field, String expectedValue, String returnedValue) {
        Assertions.assertEquals(expectedValue, returnedValue);
    }

    @Step("Object {0} exists")
    public static void checkObjectIsNotNull(String objectType, Object object) {
        Assertions.assertNotNull(object);
    }

    @Step("Object {0} does not exist")
    public static void checkObjectIsNull(String objectType, Object object) {
        Assertions.assertNull(object);
    }

    @Step("The error code is {0}")
    public static void checkThatIHaveAnException(int expectedCode, int code) {
        Assertions.assertEquals(expectedCode, code);
    }

    @Step("Field {0} is equal to {1}")
    public static void checkANumber(String message, int expected, int value) {
        Assertions.assertEquals(expected, value);
    }

    @Step("Field {0} is equal to {1}")
    public static void checkANumber(String message, long expected, long value) {
        Assertions.assertEquals(expected, value);
    }

    @Step("Field {0} is equal to {1}")
    public static void checkField(String field, Double value, Double returnedValue) {
        Assertions.assertEquals(value, returnedValue);
    }

    @Step("Field {0} is equal to {1}")
    public static void checkField(String field, int value, int returnedValue) {
        Assertions.assertEquals(value, returnedValue);
    }

    @Step("Field {0} is equal to {1}")
    public static void checkField(String field, Boolean value, Boolean returnedValue) {
        Assertions.assertEquals(value, returnedValue);
    }

    @Step("Field {0} is equal to {1} Â± {3}")
    public static void checkField(String field, Double value, Double returnedValue, double delta) {
        Assertions.assertEquals(value, returnedValue, delta);
    }

    @Step("Field {0} is equal to false")
    public static void checkFalse(String field, Boolean returnedValue) {
        Assertions.assertFalse(returnedValue);
    }

    @Step("Field {0} is Class {1}")
    public static void checkClass(String field, Class expected, Class actual) {
        Assertions.assertEquals(expected, actual);
    }

    @Step("Field {0} fail, {1}")
    public static void fail(String field, String message) {
        Assertions.fail(message);
    }


}
