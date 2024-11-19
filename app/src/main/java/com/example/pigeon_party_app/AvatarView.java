package com.example.pigeon_party_app;

import static android.graphics.Color.parseColor;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * This class creates a default avatar with the user's first initial
 * Based on AvatarView - Custom Implementation of ImageView, Muthu Raj:
 * https://medium.com/android-news/avatarview-custom-implementation-of-imageview-4bcf0714d09d
 */
public class AvatarView extends AppCompatImageView {

    Path clipPath;
    Drawable background;
    String initial;
    TextPaint initialColour;
    Paint backgroundColour;

    User user;

    RectF rectF;

//    public AvatarView(@NonNull Context context) {
//        super(context);
//        init();
//    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

//    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }

    protected void init() {

        rectF = new RectF();
        clipPath = new Path();

//        int imageSize = getResources().getDimensionPixelSize(R.dimen.avatar_size);

        backgroundColour = new Paint(Paint.ANTI_ALIAS_FLAG);
        initialColour = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        initialColour.setTextSize(50f * getResources().getDisplayMetrics().scaledDensity);
        initialColour.setColor(Color.WHITE);
    }

    public void setUser(User user) {
        this.user = user;
        setValues();
    }

    private void setValues() {
        backgroundColour.setColor(parseColor(user.getColour()));
        initial = user.getName().substring(0, 1);
        setDrawable();
    }

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
}
