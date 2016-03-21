package me.spirit.talk.eventbusdemo.util;

import android.widget.EditText;

public class TextUtils {

    private TextUtils() { /* cannot be instantiated */}

    /**
     * 
     * @param et
     * @return {@link EditText#getEditableText()} if not empty, else return {@link EditText#getHint()}
     */
    public static CharSequence getHintIfTextIsNull(EditText et) {
        CharSequence text;
        if (et == null) {
            return null;
        }

        return (android.text.TextUtils.isEmpty(text = et.getEditableText().toString())) ? et.getHint() : text;
    }
}
