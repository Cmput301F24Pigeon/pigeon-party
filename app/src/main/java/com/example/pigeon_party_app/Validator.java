package com.example.pigeon_party_app;

import android.widget.EditText;
import org.apache.commons.validator.routines.EmailValidator;

/**
Purpose: this is to ensure the user input is valid. The validator needs to be customizable (custom error messages)
because this class is needed to validate several attributes.
 */
public class Validator {

    /**
     * Function checks if an EditText field is empty, and creates an error in the UI if it is
     * @param field EditText object being checked
     * @param errorMessage String object that will be displayed if the EditText field is empty
     * @return true if the field is empty, false if the field has been filled in
     */
    public static boolean isEmpty(EditText field, String errorMessage) {
        String input = field.getText().toString().trim();

        if (input.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return true;
        }

        return false;
    }

    /**
     * Function checks if an EditText field is a name, and creates an error in the UI if it's not
     * @param field EditText object being checked
     * @param errorMessage String object that will be displayed if the EditText field is not a name
     * @return true if the field is a name, false if the field is empty or contains an invalid character
     */
    public static boolean isName(EditText field, String errorMessage) {

        String input = field.getText().toString().trim();

        if (input.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Function checks if an EditText field is an email, and creates an error in the UI if it's not
     * @param field EditText object being checked
     * @param errorMessage String object that will be displayed if the EditText field is not an email
     * @return true if the field is an email, false if the field is empty or isn't a valid email
     */
    public static boolean isEmail(EditText field, String errorMessage) {

        String input = field.getText().toString().trim();

        EmailValidator validator = EmailValidator.getInstance();

        if (input.isEmpty() || !validator.isValid(input)) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Function checks if an EditText field is a phone number, and creates an error in the UI if it's not
     *
     * @param field        EditText object being checked
     * @param errorMessage String object that will be displayed if the EditText field is not a phone number
     * @return true if the field is a phone number, false if the field doesn't contain either 0 or 10 digits
     */
    public static boolean isPhoneNumber(EditText field, String errorMessage) {
        String input = field.getText().toString().trim();

        // https://stackoverflow.com/questions/14392270/how-do-i-check-if-a-string-contains-any-characters-from-a-given-set-of-character
        if (!input.matches(".*[1234567890].*") && input.length() != 10 && !input.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }

        return true;
    }
}