package com.strakswallet.presenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.strakswallet.R;
import com.strakswallet.presenter.activities.util.BRActivity;
import com.strakswallet.tools.animation.BRAnimator;
import com.strakswallet.tools.animation.SpringAnimator;
import com.strakswallet.tools.manager.BRSharedPrefs;
import com.strakswallet.tools.security.AuthManager;
import com.strakswallet.tools.security.BRKeyStore;
import com.strakswallet.tools.util.BRConstants;

import java.util.Locale;


public class DisabledActivity extends BRActivity {
    private static final String TAG = DisabledActivity.class.getName();
    private TextView untilLabel;
    private TextView disabled;
    //    private TextView attempts;
    private ConstraintLayout layout;
    private Button resetButton;
    private CountDownTimer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled);

        untilLabel = (TextView) findViewById(R.id.until_label);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        disabled = (TextView) findViewById(R.id.disabled);
//        attempts = (TextView) findViewById(R.id.attempts_label);
        resetButton = (Button) findViewById(R.id.reset_button);

        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRAnimator.showSupportFragment(DisabledActivity.this, BRConstants.walletDisabled);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisabledActivity.this, InputWordsActivity.class);
                intent.putExtra("resetPin", true);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        untilLabel.setText("");

    }

    private void refresh() {
        if (AuthManager.getInstance().isWalletDisabled(DisabledActivity.this)) {
            SpringAnimator.failShakeAnimation(DisabledActivity.this, disabled);
        } else {
            BRAnimator.startBreadActivity(DisabledActivity.this, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long disabledUntil = AuthManager.getInstance().disabledUntil(this);
//        Log.e(TAG, "onResume: disabledUntil: " + disabledUntil + ", diff: " + (disabledUntil - BRSharedPrefs.getSecureTime(this)));
        long disabledTime = disabledUntil - System.currentTimeMillis();
        int seconds = (int) disabledTime / 1000;
        int ms = (int) disabledTime % 1000;
        timer = new CountDownTimer((disabledTime + 999), 1000) {
            public void onTick(long millisUntilFinished) {
                long durationSeconds = (millisUntilFinished / 1000) - 1;
                untilLabel.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", durationSeconds / 3600,
                        (durationSeconds % 3600) / 60, (durationSeconds % 60)));
                if (durationSeconds == 0)
                {
                    this.cancel();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                        }
                    },50);
                }
            }

            // onFinish is lagy
            public void onFinish() { }
        };
        if(ms>550) seconds++;
        untilLabel.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600,(seconds % 3600) / 60, (seconds % 60)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.start();
            }
        },ms);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (AuthManager.getInstance().isWalletDisabled(DisabledActivity.this)) {
            SpringAnimator.failShakeAnimation(DisabledActivity.this, disabled);
        } else {
            BRAnimator.startBreadActivity(DisabledActivity.this, true);
        }
        overridePendingTransition(R.anim.fade_up, R.anim.fade_down);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
}
