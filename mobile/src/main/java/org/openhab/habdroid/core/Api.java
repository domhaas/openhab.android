package org.openhab.habdroid.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONObject;
import org.openhab.habdroid.ui.OpenHABMainActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by dom on 25.12.2016.
 */

public class Api implements TextToSpeech.OnInitListener {
    private OpenHABMainActivity MainActivity;
    private SharedPreferences mSettings;
    private Context context;
    private TextToSpeech tts_engine;
    private double tts_pitch = 1.0;
    private double tts_speed = 1.0;
    private AsyncHttpServer server;
    private static final String TAG = Api.class.getSimpleName();

    public Api(OpenHABMainActivity activity) {
        MainActivity = activity;
        mSettings = PreferenceManager.getDefaultSharedPreferences(MainActivity);
        context = MainActivity.getApplicationContext();
        tts_engine = new TextToSpeech(MainActivity, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts_engine.setPitch((float) tts_pitch);
            tts_engine.setSpeechRate((float) tts_speed);
        }
    }

    public boolean process() {
        if (isEnabled()) {
            init();
            return true;
        } else {
            return false;
        }
    }

    private void init() {
        initWebserviceServer();
        initWebserviceEndpoints();
        initWebserviceListener();
    }

    private void initWebserviceServer() {
        Log.d(TAG, "Local IP Address: "+getLocalIpAddress());
        server = new AsyncHttpServer();
    }

    private void initWebserviceEndpoints() {
        server.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send("HABDroid Webservice is running");
            }
        });

        server.get("/display/off", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                if (isDisplayOn()) {
                    setDisplayOff();
                }
                response.send("");
            }
        });

        server.get("/display/on", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
            if (!isDisplayOn()) {
                setDisplayOn();
            }
            response.send("");
            }
        });

        server.get("/display", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                if (isDisplayOn()) {
                    response.send("ON");
                } else {
                    response.send("OFF");
                }
            }
        });

        server.get("/notify/sound", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.send("");
            }
        });

        server.get("/speak", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                if (request.getQuery().containsKey("text")) {
                    speak(request.getQuery().getString("text"));
                }
                response.send("");
            }
        });
    }



    private void initWebserviceListener() {
        server.listen(getPort());
    }

    private boolean isEnabled() {
        boolean api_enabled = mSettings.getBoolean("default_openhab_api_enabled", false);
        return api_enabled;
    }

    private Integer getPort() {
        String api_port = mSettings.getString("default_openhab_api_port", "5000");
        return Integer.parseInt(api_port);
    }

    private void setDisplayOff() {
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("input keyevent KEYCODE_SLEEP\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        }catch(IOException e){
        }catch(InterruptedException e){
        }
    }

    private void setDisplayOn() {
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("input keyevent KEYCODE_WAKEUP\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        }catch(IOException e){
        }catch(InterruptedException e){
        }
    }

    private boolean isDisplayOn() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (pm.isInteractive()) {
                return true;
            } else {
                return false;
            }
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH){
            if(pm.isScreenOn()){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void speak(String text) {
        tts_engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }



    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration <InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

}