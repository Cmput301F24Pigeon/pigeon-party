package com.example.pigeon_party_app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Test class for the main functions in the AvatarView class
 */
@RunWith(AndroidJUnit4.class)
public class AvatarViewTest {

    private AvatarView avatarView;
    private User testUser;

    /**
     * Method to be performed before the test to ensure the necessary attributes are created first
     */
    @Before
    public void setUp() {
        avatarView = new AvatarView(InstrumentationRegistry.getInstrumentation().getContext(), null);
        testUser = new User("test-user-name", "test@email.com", null, "test-user-id", true, true, null, false, "#000000", new ArrayList<String>(), new ArrayList<String>(), false);
    }

    /**
     * Tests creating a default avatar with the user's initial and background colour
     */
    @Test
    public void testInitialAvatarCreation() {
        avatarView.setUser(testUser);

        String initial = AvatarView.getInitial();

        assertEquals("t", initial);
        assertEquals(Color.parseColor("#000000"), avatarView.backgroundColour.getColor());
    }

    /**
     * Tests that the onDraw method draws something on the canvas
     */
    @Test
    public void testOnDraw_DefaultAvatar() {
        avatarView.setUser(testUser);
        Canvas mockCanvas = new Canvas();

        avatarView.onDraw(mockCanvas);

        assertNotNull(avatarView.background);
    }

    /**
     * Tests downloading an image from a url and setting is as the image for the avatar
     */
    @Test
    public void testProfilePicDownload_Success() {
        testUser.setProfileImagePath(null);
        avatarView.setUser(testUser);
        String mockImageUrl = "https://some.url.com/image.jpg";
        Bitmap mockBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        avatarView.downloadImage(mockImageUrl, avatarView);

        avatarView.setImageBitmap(mockBitmap);

        assertNotNull(avatarView.getDrawable());
    }

    /**
     * Tests an error downloading an image, checks that an avatar is still drawn
     */
    @Test
    public void testProfilePicDownload_Failure() {
        testUser.setProfileImagePath(null);
        avatarView.setUser(testUser);

        avatarView.getProfilePic(testUser);

        assertNotNull(avatarView.background);
    }

    /**
     * Tests that avatarView is drawn at specified size
     */
    @Test
    public void testOnMeasure() {
        avatarView.onMeasure(100, 100);

        assertEquals(0f, avatarView.rectF.left, 0);
        assertEquals(100f, avatarView.rectF.right, 0);
        assertEquals(100f, avatarView.rectF.bottom, 0);
    }

    /**
     * Tests functionality of setDrawable function
     * Checks that a drawable is created with user's initial and background colour
     */
    @Test
    public void testSetDrawable() {
        avatarView.setUser(testUser);

        avatarView.setDrawable();

        assertNotNull(avatarView.background);
        assertEquals("t", AvatarView.getInitial());
        assertEquals(Color.parseColor("#000000"), avatarView.backgroundColour.getColor());
    }
}