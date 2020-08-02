package com.strakswallet.presenter.activities.settings;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.strakswallet.R;
import com.strakswallet.presenter.activities.util.BRActivity;
import com.strakswallet.presenter.entities.CurrencyEntity;
import com.strakswallet.tools.animation.BRAnimator;
import com.strakswallet.tools.manager.BRApiManager;
import com.strakswallet.tools.manager.BRSharedPrefs;
import com.strakswallet.tools.manager.FontManager;
import com.strakswallet.tools.sqlite.CurrencyDataSource;
import com.strakswallet.tools.util.BRConstants;
import com.strakswallet.tools.util.CurrencyUtils;
import com.strakswallet.wallet.WalletsMaster;
import com.strakswallet.wallet.abstracts.BaseWalletManager;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Timer;
import java.util.TimerTask;


public class DisplayCurrencyActivity extends BRActivity {
    private static final String TAG = DisplayCurrencyActivity.class.getName();
    private TextView exchangeText;
    private ListView listView;
    private CurrencyListAdapter adapter;
    //    private String ISO;
//    private float rate;
    public static boolean appVisible = false;
    private static DisplayCurrencyActivity app;
    private Button leftButton;
    private Button rightButton;
    BaseWalletManager mWalletManager;

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;

    public static DisplayCurrencyActivity getApp() {
        return app;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_currency);

        ImageButton faq = findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRAnimator.showSupportFragment(app, BRConstants.displayCurrency);
            }
        });

        mWalletManager = WalletsMaster.getInstance(this).getCurrentWallet(this);

        exchangeText = findViewById(R.id.exchange_text);
        listView = findViewById(R.id.currency_list_view);
        adapter = new CurrencyListAdapter(this);
        adapter.addAll(CurrencyDataSource.getInstance(this).getAllCurrencies(this, WalletsMaster.getInstance(this).getCurrentWallet(this).getIso(this)));
        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButton(true);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButton(false);
            }
        });

        int unit = BRSharedPrefs.getCryptoDenomination(this, "STAK"); // any iso, using one for all for now
        if (unit == BRConstants.CURRENT_UNIT_BITS) {
            setButton(true);
        } else {
            setButton(false);
        }

        initializeTimerTask(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BRApiManager.getInstance().runTimerTask();
                TextView currencyItemText = (TextView) view.findViewById(R.id.currency_item_text);
                final String selectedCurrency = currencyItemText.getText().toString();
                String iso = selectedCurrency.substring(0, 3);
                BRSharedPrefs.putPreferredFiatIso(DisplayCurrencyActivity.this, iso);
            }

        });
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Find selected and focus it
        for(int i=0;i<adapter.getCount();i++)
            if(adapter.getItem(i).code.equals(BRSharedPrefs.getPreferredFiatIso(this)))
            {
                listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                listView.setSelectionFromTop(i, listView.getMeasuredHeight()*3);
                break;
            }
    }

    private void initializeTimerTask(final Context context) {
        timer = new Timer();
        handler = new Handler();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateExchangeRate();
                            }
                        });
                    }
                });
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    public void startTimer() {
        if (timer == null) initializeTimerTask(this);
        timer.schedule(timerTask, 1000, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer=null;
        }
    }
    private void updateExchangeRate() {
        //set the rate from the last saved
        String iso = BRSharedPrefs.getPreferredFiatIso(this);
        CurrencyEntity entity = CurrencyDataSource.getInstance(this).getCurrencyByCode(this, WalletsMaster.getInstance(this).getCurrentWallet(this).getIso(this), iso);
        if (entity != null) {
            String formattedExchangeRate = CurrencyUtils.getFormattedAmount(DisplayCurrencyActivity.this, BRSharedPrefs.getPreferredFiatIso(this), new BigDecimal(entity.rate));
            exchangeText.setText(String.format("%s = %s", CurrencyUtils.getFormattedAmount(this, mWalletManager.getIso(this), new BigDecimal(100000000)), formattedExchangeRate));
        }
        adapter.notifyDataSetChanged();
    }

    private void setButton(boolean left) {
        if (left) {
            BRSharedPrefs.putCryptoDenomination(this, mWalletManager.getIso(this), BRConstants.CURRENT_UNIT_BITS);
            leftButton.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.white));
            leftButton.setBackground(getDrawable(R.drawable.b_half_left_blue));
            rightButton.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.dark_blue));
            rightButton.setBackground(getDrawable(R.drawable.b_half_right_blue_stroke));
        } else {
            BRSharedPrefs.putCryptoDenomination(this, mWalletManager.getIso(this), BRConstants.CURRENT_UNIT_STRAKS);
            leftButton.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.dark_blue));
            leftButton.setBackground(getDrawable(R.drawable.b_half_left_blue_stroke));
            rightButton.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.white));
            rightButton.setBackground(getDrawable(R.drawable.b_half_right_blue));
        }
        updateExchangeRate();

    }

    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public class CurrencyListAdapter extends ArrayAdapter<CurrencyEntity> {
        public final String TAG = CurrencyListAdapter.class.getName();

        private final Context mContext;
        private final int layoutResourceId;
        private TextView textViewItem;
        private final Point displayParameters = new Point();

        public CurrencyListAdapter(Context mContext) {

            super(mContext, R.layout.currency_list_item);

            this.layoutResourceId = R.layout.currency_list_item;
            this.mContext = mContext;
            ((AppCompatActivity) mContext).getWindowManager().getDefaultDisplay().getSize(displayParameters);
//        currencyListAdapter = this;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String oldIso = BRSharedPrefs.getPreferredFiatIso(mContext);
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((AppCompatActivity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            // get the TextView and then set the text (item name) and tag (item ID) values
            textViewItem = convertView.findViewById(R.id.currency_item_text);
            FontManager.overrideFonts(textViewItem);
            String iso = getItem(position).code;
            Currency c = null;
            try {
                c = Currency.getInstance(iso);
            } catch (IllegalArgumentException ignored) {
            }
            textViewItem.setText(c == null ? iso : String.format("%s (%s)", iso, c.getSymbol()));
            ImageView checkMark = convertView.findViewById(R.id.currency_checkmark);

            if (iso.equalsIgnoreCase(oldIso)) {
                checkMark.setVisibility(View.VISIBLE);
            } else {
                checkMark.setVisibility(View.GONE);
            }
            normalizeTextView();
            return convertView;

        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return IGNORE_ITEM_VIEW_TYPE;
        }

        private boolean isTextSizeAcceptable(TextView textView) {
            textView.measure(0, 0);
            int textWidth = textView.getMeasuredWidth();
            int checkMarkWidth = 76 + 20;
            return (textWidth <= (displayParameters.x - checkMarkWidth));
        }

        private boolean normalizeTextView() {
            int count = 0;
//        Log.d(TAG, "Normalizing the text view !!!!!!");
            while (!isTextSizeAcceptable(textViewItem)) {
                count++;
                float textSize = textViewItem.getTextSize();
//            Log.e(TAG, "The text size is: " + String.valueOf(textSize));
                textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 2);
                this.notifyDataSetChanged();
            }
            return (count > 0);
        }

    }


}
