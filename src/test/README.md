# Automatic Test Considerations #

## How to use Allure annotation in Java

The minimum annotations are @TmsLink and @Feature at class level and @Description and @Step for each test.
You can use @Step("Action: ...") to identify that a step is an Action. Otherwise, all the @Steps will be considered as Expected Results.

## What to test - Recommendations

You need to decide if it's necessary to report the unit test as formal.
For example: The class TokenGeneratorTest.java does not require to have formal documentation.

Do not document the util methods. Otherwise, you will have so much documentation in the Test Case.
For example: The function getPatientData() at PatientClientTest.java is just an util to get a PatientData object and it's not required to have add the @Step("Action: ")



