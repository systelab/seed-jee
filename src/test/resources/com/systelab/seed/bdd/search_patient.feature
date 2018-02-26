Feature: Patient search
  To allow a user to find patients quickly, the application must offer multiple ways to search for a patient.

  Scenario: Search patients by year of birth
    Given a patient with the name 'Josh', surname 'Long', born in 14 March 1982
      And another patient with the name 'Mark', surname 'Reinhold', born in 23 August 1972
    When the user searches for patients born between 1970 and 1985
    Then 2 patients should have been found
      And Patient 1 should have the name 'Josh'
      And Patient 2 should have the name 'Mark'