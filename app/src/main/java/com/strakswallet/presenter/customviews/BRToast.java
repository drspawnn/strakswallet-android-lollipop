package com.strakswallet.presenter.customviews;

import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.strakswallet.BreadApp;
import com.strakswallet.R;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 3/20/17.
 * Copyright (c) 2017 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class BRToast {
    private static boolean customToastAvailable = true;
    private static String oldMessage;
    private static Toast toast;

    /**
     * Shows a custom toast using the given string as a paramater,
     *
     * @param message the message to be shown in the custom toast
     */

    public static void showCustomToast(Context app, String message, int yOffSet, int duration, int layoutDrawable) {
        if (app == null) return;
        if (!(app instanceof AppCompatActivity)) app = BreadApp.getBreadContext();
        if (app == null) return;
        if (toast == null) toast = new Toast(app);
        if (!BreadApp.isAppInBackground(app)) return;

        if (customToastAvailable || !oldMessage.equals(message)) {
            createToast(app, message, yOffSet, duration, layoutDrawable);
        }
    }

    public static void showCustomToast(Context app, String message, int yOffSet, int duration, int layoutDrawable, boolean showOnlyBackground) {
        if (app == null) return;
        if (!(app instanceof AppCompatActivity)) app = BreadApp.getBreadContext();
        if (app == null) return;
        if (toast == null) toast = new Toast(app);
        if (showOnlyBackground) if (!BreadApp.isAppInBackground(app)) return;

        if (customToastAvailable || !oldMessage.equals(message)) {
            createToast(app, message, yOffSet, duration, layoutDrawable);
        }
    }

    public static void createToast(Context app, String message, int yOffSet, int duration, int layoutDrawable){
        oldMessage = message;
        customToastAvailable = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customToastAvailable = true;
            }
        }, 1000);
        LayoutInflater inflater = ((AppCompatActivity) app).getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) ((AppCompatActivity) app).findViewById(R.id.toast_layout_root));
        layout.setBackgroundResource(layoutDrawable);
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        toast.setGravity(Gravity.TOP, 0, yOffSet);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    public static boolean isToastShown() {
        return toast != null && toast.getView() != null && toast.getView().isShown();
    }
}
