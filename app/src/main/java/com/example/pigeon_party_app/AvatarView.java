package com.example.pigeon_party_app;

import static android.graphics.Color.parseColor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;

/**
 * This class creates a default avatar with the user's first initial
 * Based on AvatarView - Custom Implementation of ImageView, Muthu Raj:
 * https://medium.com/android-news/avatarview-custom-implementation-of-imageview-4bcf0714d09d
 */
public class AvatarView extends AppCompatImageView {

    private float TEXT_SIZE = 50f;
    private int TEXT_COLOR = Color.WHITE;

    private Path clipPath;
    private Drawable background;
    private static String initial;
    private TextPaint initialColour;
    private Paint backgroundColour;

    private User user;

    private RectF rectF;

//    public AvatarView(@NonNull Context context) {
//        super(context);
//        init();
//    }

    /**
     * Constructor function for Avatar view class, calls constructor for ImageView class
     * @param context
     * @param attrs
     */
    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

//    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }

    /**
     * Getter for the initial that is displayed in the avatar, mainly for testing
     * @return The initial as a 1 character String
     */
    public static String getInitial() {
        return initial;
    }

    /**
     * Initializes fields in AvatarView class and sets text colour and size for initials
     */
    protected void init() {
        rectF = new RectF();
        clipPath = new Path();
        backgroundColour = new Paint(Paint.ANTI_ALIAS_FLAG);
        initialColour = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        initialColour.setTextSize(TEXT_SIZE * getResources().getDisplayMetrics().scaledDensity);
        initialColour.setColor(TEXT_COLOR);
    }

    /**
     * Sets the user that the avatar is for
     * @param user the User object to create the avatar from
     */
    public void setUser(User user) {
        this.user = user;
        setValues();
    }

    /**
     * Sets the background colour and initial for the drawable
     */
    private void setValues() {
        // Parsing hexadecimal colours:
        // https://stackoverflow.com/questions/25837449/setbackgroundcolor-with-hex-color-codes-androidstudio
        backgroundColour.setColor(parseColor(user.getColour()));
        initial = user.getName().substring(0, 1);
        setDrawable();
        getProfilePic(user);
    }

    /**
     * Draws the background colour and text on a canvas and stores it in a drawable
     */
    private void setDrawable() {
        background = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {

                int centerX = Math.round(canvas.getWidth() * 0.5f);
                int centerY = Math.round(canvas.getHeight() * 0.5f);

                float textWidth = initialColour.measureText(initial) * 0.5f;
                float textBaselineHeight = initialColour.getFontMetrics().ascent * -0.4f;

                canvas.drawCircle(centerX,
                        centerY,
                        Math.max(canvas.getHeight() / 2, textWidth / 2),
                        backgroundColour);

                canvas.drawText(initial, centerX - textWidth, centerY + textBaselineHeight, initialColour);
            }

            // Functions below are needed for setDrawable function
            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        rectF.set(0, 0, screenWidth, screenHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        background.setBounds(0, 0, getWidth(), getHeight());
        background.draw(canvas);

        clipPath.reset();
        clipPath.addCircle(rectF.centerX(), rectF.centerY(), (rectF.height() / 2), Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }

    private void getProfilePic(User user) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        StorageReference imageRef = storageRef.child("profile_images/" + user.getUniqueId());
//
//        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            downloadImage(uri.toString(), this);
//        }).addOnFailureListener(exception -> {
//            setDrawable();
//            invalidate();  // Force a redraw with the default avatar
//        });

        if (user.getProfileImagePath() == null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("profile_images/" + user.getUniqueId());

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadImage(uri.toString(), this);
            }).addOnFailureListener(exception -> {
                setDrawable();  // If there's an error (e.g., image not found), use the default avatar
                invalidate();    // Force a redraw with the default avatar
            });
        } else {
            post(() -> this.setImageBitmap(user.getProfileImagePath()));
        }
    }

    private void downloadImage(String imageUrl, final ImageView imageView) {
        new Thread(() -> {
            try {
                // Download image using URL
                InputStream inputStream = new URL(imageUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                user.setProfileImagePath(bitmap);

                // Run on UI thread to update ImageView
                post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
