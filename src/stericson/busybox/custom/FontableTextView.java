package stericson.busybox.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FontableTextView extends TextView {

    public FontableTextView(Context context) {
        super(context);

        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/default.ttf");
            if (this instanceof TextView) {
                ((TextView) ((View) this)).setTypeface(tf);
            } else {
                ((Button) ((View) this)).setTypeface(tf);
            }
        } catch (Exception ignore) {
        }

    }

    public FontableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/default.ttf");
            if (this instanceof TextView) {
                ((TextView) ((View) this)).setTypeface(tf);
            } else {
                ((Button) ((View) this)).setTypeface(tf);
            }
        } catch (Exception ignore) {
        }
    }

    public FontableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/default.ttf");
            if (this instanceof TextView) {
                ((TextView) ((View) this)).setTypeface(tf);
            } else {
                ((Button) ((View) this)).setTypeface(tf);
            }
        } catch (Exception ignore) {
        }
    }
}
