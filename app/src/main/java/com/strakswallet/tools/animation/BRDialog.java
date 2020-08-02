package com.strakswallet.tools.animation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;

import com.strakswallet.R;
import com.strakswallet.presenter.customviews.BRDialogView;
import com.strakswallet.tools.threads.executor.BRExecutor;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 3/15/17.
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
public class BRDialog {
    private static final String TAG = BRDialog.class.getName();
    private static BRDialogView dialog;
    public static Boolean textLink=false;

    /**
     * Safe from any threads
     *
     * @param app needs to be activity
     */
    public static void showCustomDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message,
                                        @NonNull final String posButton, final String negButton, final BRDialogView.BROnClickListener posListener,
                                        final BRDialogView.BROnClickListener negListener, final DialogInterface.OnDismissListener dismissListener, final int iconRes) {
        if (((AppCompatActivity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new BRDialogView();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setDismissListener(dismissListener);
                dialog.setIconRes(iconRes);
                dialog.setCancelable(true);
                dialog.show(((AppCompatActivity) app).getSupportFragmentManager(), dialog.getClass().getName());
            }
        });

    }

    public static void showCustomDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message,
                                        @NonNull final String posButton, final String negButton, final BRDialogView.BROnClickListener posListener,
                                        final BRDialogView.BROnClickListener negListener, final DialogInterface.OnDismissListener dismissListener, final int iconRes, final boolean isCancelable) {
        if (((AppCompatActivity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new BRDialogView();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setDismissListener(dismissListener);
                dialog.setIconRes(iconRes);
                dialog.setCancelable(isCancelable);
                dialog.show(((AppCompatActivity) app).getSupportFragmentManager(), dialog.getClass().getName());
            }
        });
    }

    public static void showHelpDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message, @NonNull final String posButton, @NonNull final String negButton, final BRDialogView.BROnClickListener posListener, final BRDialogView.BROnClickListener negListener, final BRDialogView.BROnClickListener helpListener) {

        if (((AppCompatActivity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new BRDialogView();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setHelpListener(helpListener);
                dialog.showHelpIcon(true);
                dialog.show(((AppCompatActivity) app).getSupportFragmentManager(), dialog.getClass().getName());

            }
        });
    }

    public static void showSimpleDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message) {
        showCustomDialog(app, title, message, app.getString(R.string.AccessibilityLabels_close), null, new BRDialogView.BROnClickListener() {
            @Override
            public void onClick(BRDialogView brDialogView) {
                brDialogView.dismissWithAnimation();
            }
        }, null, null, 0);
    }

    //same but with a SpannableString as message to be able to click on a portion of the text with a listener
    public static void showCustomDialog(@NonNull final Context app, @NonNull final String title, @NonNull final SpannableString message,
                                        @NonNull final String posButton, final String negButton, final BRDialogView.BROnClickListener posListener,
                                        final BRDialogView.BROnClickListener negListener, final DialogInterface.OnDismissListener dismissListener, final int iconRes) {
        if (((AppCompatActivity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new BRDialogView();
                dialog.setTitle(title);
                dialog.setSpan(message);//setting Span instead of String
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setDismissListener(dismissListener);
                dialog.setIconRes(iconRes);
                dialog.show(((AppCompatActivity) app).getSupportFragmentManager(), dialog.getClass().getName());
            }
        });
    }

    public static void hideDialog() {
        if (dialog != null) dialog.dismiss();
    }
}
