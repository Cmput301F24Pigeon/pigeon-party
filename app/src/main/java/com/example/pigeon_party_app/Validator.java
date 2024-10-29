package com.example.pigeon_party_app;

import android.widget.EditText;
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
}