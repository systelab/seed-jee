# Automatic Test Considerations #

## What to test - Recommendations

As a first step, you need to consider the verification against the specifications, how the REST API is build in terms of structure, objects and methods.
You can get the endpoints at http://127.0.0.1:13080/seed/swagger/ as follows:
1) Perform a Login with the user Systelab/Systelab. The authorization token is in the Response header in the "authorization", for example: "Bearer eyJhbGciOiJIUzUxMiJ9..."
2) Authorize the user by clicking on the "Authorize" button (at the top of the page) and enter the authorization token.
3) Once the Systelab user is authorized, you can access the REST API.

You need to decide if it's necessary to report the unit test as formal.
For example: The class TokenGeneratorTest.java does not require to have formal documentation.

Do not document the util methods. Otherwise, you will have so much documentation in the Test Case.
For example: The function getPatientData() at PatientClientTest.java is just an util to get a PatientData object and it's not required to have add the @Step("Action: ")

## How to use Allure annotation in Java

The annotations are @TmsLink and @Feature at class level and @Description and @Step for each test. Refer to the class PatientResourceTest.java.
- @TmsLink: Name of the Test Case.
-- It must be the same as the Test Case name in Jama
-- We highly recommend you not to use the @TmsLink for additional traceability purposes. It's better to keep it simple so that it is maintainable
- @Feature: Description of the Test Case. You can also add additional information as text such as preconditions, environment, etc.
- @Description: Step Action in the Test Case. You must enter the action to perform.
- @Step: Step Expected Result or Action in the Test Case.
-- You can use @Step("Action: ...") to identify that a step is an Action. Otherwise, all the @Steps will be considered as Expected Results.
-- Consider that it may be nested Steps
-- All the Expected Results are documented just once, for each type of object. Refer to the class TestUtil.java





