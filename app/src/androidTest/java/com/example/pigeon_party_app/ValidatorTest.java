package com.example.pigeon_party_app;

import android.content.Context;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import androidx.test.platform.app.InstrumentationRegistry;

/**
 * This tests our validator class to see if it valids properly
 */
public class ValidatorTest {

    private EditText editText;
    private String emailError = "Invalid email address";
    private String nameError = "Invalid name";
    private String phoneError = "Invalid phone";

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        editText = new EditText(context);
    }

    /**
     * This tests to see if the email is empty
     */
    @Test
    public void testEmail_EmptyInput_ReturnsFalse() {
        editText.setText("");

        boolean result = Validator.isEmail(editText, emailError);

        assertFalse(result);
        assertEquals(emailError, editText.getError());
    }
    /**
     * This tests if our email is invalid
     */
    @Test
    public void testEmail_InvalidEmail_ReturnsFalse() {
        editText.setText("invalid-email");

        boolean result = Validator.isEmail(editText, emailError);

        assertFalse(result);
        assertEquals(emailError, editText.getError());
    }

    /**
     * This tests our email is valid
     */
    @Test
    public void testEmail_ValidEmail_ReturnsTrue() {
        editText.setText("test@example.com");

        boolean result = Validator.isEmail(editText, emailError);

        assertTrue(result);
        assertNull(editText.getError());
    }

    /**
     * This tests to see if our name is empty
     */
    @Test
    public void testName_EmptyInput_ReturnsFalse() {
        editText.setText("");

        boolean result = Validator.isName(editText, nameError);

        assertFalse(result);
        assertEquals(nameError, editText.getError());
    }

    /**
     * This tests to see if our name is empty and it will return true
     */
    @Test
    public void testName_ValidName_ReturnsTrue() {
        editText.setText("test@example.com");

        boolean result = Validator.isName(editText, nameError);

        assertTrue(result);
        assertNull(editText.getError());
    }

    /**
     * This tests to see if our phone number is invalid
     */
    @Test
    public void testPhoneNumber_InvalidInput_ReturnsFalse() {
        editText.setText("123");

        boolean result = Validator.isPhoneNumber(editText, phoneError);

        assertFalse(result);
        assertEquals(phoneError, editText.getError());
    }

    /**
     * This tests to see if our phone number is empty
     */
    @Test
    public void testPhoneNumber_EmptyInput_ReturnsTrue() {
        editText.setText("");

        boolean result = Validator.isPhoneNumber(editText, phoneError);

        assertTrue(result);
        assertNull(editText.getError());
    }

    /**
     * This tests to see if our phone number is valid
     */
    @Test
    public void testPhoneNumber_ValidInput_ReturnsTrue() {
        editText.setText("1234567890");

        boolean result = Validator.isPhoneNumber(editText, phoneError);

        assertTrue(result);
        assertNull(editText.getError());
    }
}
