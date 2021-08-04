package com.systelab.seed.allure;

import com.systelab.seed.utils.TestUtil;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;

@TmsLink("TC000X_Allure-Validation_java")
@Feature("User Test Suite.\n\nGoal:\nThis test case is intended to verify the correct ....\n\nEnvironment:\n...\nPreconditions:\nN/A.")
public class AllureValidationTest
{
  @Description("Allure-validation - Trivial action with expected result (pass)")
  @Test
  public void trivialActionWithExpectedResultPass()
  {
    TestUtil.checkTrue("True", true);
  }

  @Description("Allure-validation - Trivial action with expected result (fail)")
  @Test
  public void trivialActionWithExpectedResultFail()
  {
    TestUtil.checkTrue("True", false);
  }

  @Step("Action:First Level action")
  private void firstLevelAction(boolean pass)
  {
    secondLevelAction(pass);
  }

  @Step("Action:Second Level action")
  private void secondLevelAction(boolean pass)
  {
    thirdLevelAction(pass);
  }

  @Step("Action:Third Level action")
  private void thirdLevelAction(boolean pass)
  {
    fourthLevelAction(pass);
  }

  @Step("Action:Fourth Level action")
  private void fourthLevelAction(boolean pass)
  {
    TestUtil.checkTrue("True", pass);
  }

  @Step("First Level result")
  private void firstLevelResult(boolean pass)
  {
    Assert.assertTrue(true);
    secondLevelResult(pass);
  }

  @Step("Second Level result")
  private void secondLevelResult(boolean pass)
  {
    Assert.assertTrue(true);
    thirdLevelResult(pass);
  }

  @Step("Third Level result")
  private void thirdLevelResult(boolean pass)
  {
    Assert.assertTrue(true);
    fourthLevelResult(pass);
  }

  @Step("Fourth Level result")
  private void fourthLevelResult(boolean pass)
  {
    TestUtil.checkTrue("True", pass);
  }

  @Step("{0}")
  private void dummyFunction(String message)
  {
    // empty line
  }

  @Description("Allure-validation - Nested actions (pass)")
  @Test
  public void nestedActions()
  {
    firstLevelAction(true);
  }

  @Description("Allure-validation - Nested results (pass)")
  @Test
  public void nestedResults()
  {
    firstLevelResult(true);
  }

  @Description("Allure-validation - Nested actions (fail)")
  @Test
  public void nestedActionsFail()
  {
    firstLevelAction(false);
  }

  @Description("Allure-validation - Nested results (fail)")
  @Test
  public void nestedResultsFail()
  {
    firstLevelResult(false);
  }

  @Description("Allure-validation - non Nested results (fail)")
  @Test
  public void nonNestedResultsFail()
  {
    dummyFunction("first level");
    dummyFunction("2 level");
    dummyFunction("3 level");
    dummyFunction("4 level");
    TestUtil.checkTrue("false", false);
  }

  @Description("Allure-validation - non Nested results (pass)")
  @Test
  public void nonNestedResultsPass()
  {
    dummyFunction("first level");
    dummyFunction("2 level");
    dummyFunction("3 level");
    dummyFunction("4 level");
    TestUtil.checkTrue("true", true);
  }
}
