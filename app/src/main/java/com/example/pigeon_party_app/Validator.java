package com.example.pigeon_party_app;

import android.widget.EditText;
import org.apache.commons.validator.routines.EmailValidator;

/*
Purpose: this is to ensure the user input is valid. The validator needs to be customizable (custom error messages)
because this class is needed to validate several attributes.
 */
public class Validator {

    public static boolean isEmpty(EditText field, String errorMessage) {
        String input = field.getText().toString().trim();
        if (input.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return true;
        }
        return false;
    }

    public static boolean isName(String name, String errorMessage) {

        if (name.contains("\n")) {
            return false;
        }

        if (name.isEmpty()) {
            return false;
        }

        return true;
    }

    public static boolean isEmail(String email, String errorMessage) {
        if (email.isEmpty()) {
            return false;
        }

        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(email)) {
            return false;
        }

        return true;
    }

    // https://stackoverflow.com/questions/14392270/how-do-i-check-if-a-string-contains-any-characters-from-a-given-set-of-character
    public static boolean isPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches(".*[1234567890].*")) {
            return false;
        }

        if (phoneNumber.length()!= 10 && !phoneNumber.isEmpty()) {
            return false;
        }

        return true;
    }

}