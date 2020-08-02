package com.strakswallet.tools.manager;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import com.strakswallet.BreadApp;
import com.strakswallet.R;
import com.strakswallet.presenter.activities.util.ActivityUTILS;
import com.strakswallet.presenter.activities.util.BRActivity;
import com.strakswallet.presenter.customviews.BRToast;
import com.strakswallet.presenter.entities.CurrencyEntity;
import com.strakswallet.tools.sqlite.CurrencyDataSource;
import com.strakswallet.tools.threads.executor.BRExecutor;
import com.strakswallet.tools.util.Utils;
import com.strakswallet.wallet.WalletsMaster;
import com.strakswallet.wallet.abstracts.BaseWalletManager;
import com.platform.APIClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
import okhttp3.Response;

/**
 * BreadWallet
 * <p>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 7/22/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class BRApiManager {
    private static final String TAG = BRApiManager.class.getName();

    private static BRApiManager instance;
    private Timer timer;
    private OnRateUpdate listener;

    private TimerTask timerTask;

    private Handler handler;

    private BRApiManager() {
        handler = new Handler();
    }

    public static BRApiManager getInstance() {

        if (instance == null) {
            instance = new BRApiManager();
        }
        return instance;
    }

    private static String[] codes = { "USD", "JPY", "KRW", "EUR", "MXN", "GBP",
            "AUD", "BRL", "CAD", "CHF", "CLP", "CNY", "CZK", "DKK", "HKD", "HUF", "IDR",
            "ILS", "INR", "MYR", "NOK", "NZD", "PHP", "PKR", "PLN", "RUB", "SEK", "SGD",
            "THB", "TRY", "TWD", "ZAR" };

    private static String[] names = { "US Dollar", "Japanese Yen", "South Korean Won",
            "Eurozone Euro", "Mexican Peso", "Pound Sterling", "Australian Dollar", "Brazilian Real",
            "Canadian Dollar", "Swiss Franc", "Chilean Peso", "Chinese Yuan", "Czech Koruna", "Danish Krone",
            "Hong Kong Dollar", "Hungarian Forint", "Indonesian Rupiah", "Israeli Shekel", "Indian Rupee",
            "Malaysian Ringgit", "Norwegian Krone", "New Zealand Dollar", "Philippine Peso", "Pakistani Rupee",
            "Polish Zloty", "Russian Ruble", "Swedish Krona", "Singapore Dollar", "Thai Baht", "Turkish Lira",
            "New Taiwan Dollar", "South African Rand" };

    private Set<CurrencyEntity> getCurrencies(AppCompatActivity context, BaseWalletManager walletManager) {
        if (ActivityUTILS.isMainThread()) {
            throw new NetworkOnMainThreadException();
        }
        Set<CurrencyEntity> set = new LinkedHashSet<>();
        try {
            JSONArray arr = fetchRates(context, walletManager);
            updateFeePerKb(context);
            if (arr != null) {
                int length = arr.length();
                for (int i = 1; i < length; i++) {
                    CurrencyEntity tmp = new CurrencyEntity();
                    try {
                        JSONObject tmpObj = (JSONObject) arr.get(i);
                        tmp.name = tmpObj.getString("name");
                        tmp.code = tmpObj.getString("code");
                        tmp.rate = (float) tmpObj.getDouble("rate");
                        String selectedISO = walletManager.getIso(context);
                        if (tmp.code.equalsIgnoreCase(selectedISO)) {
                            BRSharedPrefs.putPreferredFiatIso(context, tmp.code);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    set.add(tmp);
                }
            } else {
                Log.e(TAG, "getCurrencies: failed to get currencies, response string: " + arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "getCurrencies: " + set.size());
        return new LinkedHashSet<>(set);
    }


    private void initializeTimerTask(final Context context) {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (BreadApp.isAppInBackground(context)) {
                                    Log.e(TAG, "doInBackground: Stopping timer, no activity on.");
                                    stopTimerTask();
                                }
                                for (BaseWalletManager w : WalletsMaster.getInstance(context).getAllWallets()) {
                                    Set<CurrencyEntity> tmp = getCurrencies((AppCompatActivity) context, w);
                                    CurrencyDataSource.getInstance(context).putCurrencies(context, w.getIso(context), tmp);
                                    if(listener!=null) listener.OnRateUpdate();
                                }
                            }
                        });
                    }
                });
            }
        };
    }

    public void runTimerTask()
    {
        timerTask.run();
    }
    public void startTimer(BRActivity app) {
        //set a new Timer
        if (timer != null) return;
        timer = new Timer();
        Log.e(TAG, "startTimer: started...");
        //initialize the TimerTask's job
        initializeTimerTask(app);

        timer.schedule(timerTask, 2000, 60000);
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static JSONArray fetchRates(final AppCompatActivity activity, BaseWalletManager walletManager) {
        StringBuilder ruse = new StringBuilder();
        String code = walletManager.getIso(activity);
        String selectedISO = BRSharedPrefs.getPreferredFiatIso(activity);

        ruse.append("{\"data\":[{\"code\":\"STAK\",\"name\":\"STRAKS\",\"rate\":1},");

        for (int i = 0; codes.length == names.length && i < codes.length; i++) {

            ruse.append("{\"code\":\"").append(codes[i]).append("\",\"name\":\"")
                    .append(names[i]).append("\",\"rate\":");

            if( codes[i].equals(selectedISO)) {

//                String myURL = "https://api.straks.info/v2/price/";//codes[i];

                String rate = fetchRateCMC(activity, codes[i]);
                if (rate.equals("0")) rate = fetchRateGECKO(activity, codes[i]);

                if (rate.equals("0"))
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utils.isEmulatorOrDebug(activity))
                                Toast.makeText(activity, "Fetch currency rate failed!", Toast.LENGTH_SHORT).show();
                            else
                                BRToast.showCustomToast(activity, "Fetch currency rate failed!",
                                        BreadApp.DISPLAY_HEIGHT_PX - 200, Toast.LENGTH_LONG, R.drawable.toast_layout_red, false);
                        }
                    });

                ruse.append(Double.parseDouble(rate));
            }
            else{
                String rate ="0";
                ruse.append(Double.parseDouble(rate));
            }

            if (i== codes.length-1)
                ruse.append("}]}");
            else
                ruse.append("},");
        }

        String jsonString = ruse.toString();

        JSONArray jsonArray = null;
        if (jsonString == null) return null;
        try {
            JSONObject obj = new JSONObject(jsonString);

            jsonArray = obj.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static String fetchRateGECKO(AppCompatActivity activity,String ticker)
    {
        String myURL = "https://api.coingecko.com/api/v3/simple/price?ids=straks&vs_currencies=" + ticker;
        String tempJsonString = urlGET(activity, myURL);

        if (tempJsonString == null) return "0";
        try {
            JSONObject tempJsonObject = new JSONObject(tempJsonString);
            tempJsonObject = tempJsonObject.getJSONObject("straks");

            return String.valueOf(tempJsonObject.getDouble(ticker.toLowerCase()));

        } catch (JSONException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String fetchRateCMC(AppCompatActivity activity,String ticker)
    {
        String myURL = "https://api.coinmarketcap.com/v1/ticker/straks/?convert=" + ticker;
        String tempJsonString = urlGET(activity, myURL);

        if (tempJsonString == null) return "0";
        try {
            JSONArray tempJsonArray = new JSONArray(tempJsonString);
            JSONObject tempJsonObject = tempJsonArray.getJSONObject(0);

            return tempJsonObject.getString("price_" + ticker.toLowerCase());

        } catch (JSONException e) {
            e.printStackTrace();
            return "0";
        }
    }

//    public static JSONArray backupFetchRates(AppCompatActivity app, BaseWalletManager walletManager) {
//        if (!walletManager.getIso(app).equalsIgnoreCase(WalletBitcoinManager.getInstance(app).getIso(app))) {
//            //todo add backup for BCH
//            return null;
//        }
//        String jsonString = urlGET(app, "https://bitpay.com/rates");
//
//        JSONArray jsonArray = null;
//        if (jsonString == null) return null;
//        try {
//            JSONObject obj = new JSONObject(jsonString);
//
//            jsonArray = obj.getJSONArray("data");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonArray;
//    }

    public static void updateFeePerKb(Context app) {
        WalletsMaster wm = WalletsMaster.getInstance(app);
        for (BaseWalletManager wallet : wm.getAllWallets()) {
            wallet.updateFee(app);
        }
    }

    public static String urlGET(Context app, String myURL) {
//        System.out.println("Requested URL_EA:" + myURL);
        if (ActivityUTILS.isMainThread()) {
            Log.e(TAG, "urlGET: network on main thread");
            throw new RuntimeException("network on main thread");
        }
        Map<String, String> headers = BreadApp.getBreadHeaders();

        Request.Builder builder = new Request.Builder()
                .url(myURL)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-agent", Utils.getAgentString(app, "android/HttpURLConnection"))
                .get();
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            builder.header((String) pair.getKey(), (String) pair.getValue());
        }

        Request request = builder.build();
        String response = null;
        Response resp = APIClient.getInstance(app).sendRequest(request, false, 0);

        try {
            if (resp == null) {
                Log.e(TAG, "urlGET: " + myURL + ", resp is null");
                return null;
            }
            response = resp.body().string();
            String strDate = resp.header("date");
            if (strDate == null) {
                Log.e(TAG, "urlGET: strDate is null!");
                return response;
            }
//            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
//            Date date = formatter.parse(strDate);
//            long timeStamp = date.getTime();
            long timeStamp = System.currentTimeMillis();
            BRSharedPrefs.putSecureTime(app, timeStamp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resp != null) resp.close();

        }
        return response;
    }

    public interface OnRateUpdate {
        void OnRateUpdate();
    }
}
