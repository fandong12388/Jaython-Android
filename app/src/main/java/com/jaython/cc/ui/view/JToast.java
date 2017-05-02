package com.jaython.cc.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jaython.cc.R;


public class JToast extends Toast {
    public static final int TOAST_DURATION = 3000;
    public static final int TOAST_DURATION_SHORT = 1500;

    private View mToast;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            WindowManager manager = (WindowManager) mToast.getContext()
                    .getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            manager.removeView(mToast);
            mToast = null;
        }
    };

    public JToast(Context context, int textResourceId, int drawableId) {
        super(context);
        Resources resources = context.getResources();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.id_cc_toast_text);
        String text = resources.getString(textResourceId);
        textView.setText(text);
        setDuration(Toast.LENGTH_SHORT);
        setView(view);
    }

    public JToast(Context context, CharSequence text, Drawable drawable) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.id_cc_toast_text);
        textView.setText(text);
        setDuration(Toast.LENGTH_SHORT);
        setView(view);
    }

    public static JToast show(String msg, Context context) {
        JToast toast = new JToast(context, msg, null);
        toast.show();
        return toast;
    }

    public static JToast show(int msg, Context context) {
        JToast toast = new JToast(context, context.getText(msg), null);
        toast.show();
        return toast;
    }

}
