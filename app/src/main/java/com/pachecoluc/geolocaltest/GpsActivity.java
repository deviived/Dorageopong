package com.pachecoluc.geolocaltest;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GpsActivity extends AppCompatActivity implements LocationListener {

    private final static int PERMISSION_REQUEST_FINE_LOC = 1312;
    private static final int PERMISSION_REQUEST_SEND_SMS = 999;

    LocationManager locationManager;
    boolean isGPSEnabled;

    boolean destination = true;
    boolean connectedHeadphones;

    int compteur = 0;
    int limite = 1;

    //LAYOUT ELEM
    TextView lng;
    TextView lat;
    TextView dest;
    Button record;

    //COORDONNEES
    double latitude;
    double longitude;
    double lat_Tacos = 49.0355476;
    double lng_Tacos = 2.0772146;

    HeadsetPlugReceiver headsetPlugReceiver;
    TextToSpeech readMe;
    String distanceR = "Votre GPS n'est pas actif";
    String text;

    Intent intentPong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        lng = findViewById(R.id.lng);
        lat = findViewById(R.id.lat);
        dest = findViewById(R.id.dist);
        record = findViewById(R.id.record);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "record",
                        Toast.LENGTH_SHORT).show();
                startSpeechToText();
            }
        });

        //headset receiver + intent filter
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_SEND_SMS);
        }

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.requestGPSLocation();

        readMe=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });

        readMe.setLanguage(Locale.FRENCH);

        intentPong = new Intent(this, MainActivity.class);
    }

    private void requestGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOC);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("__enter", "ENTER THE WU TANG!");
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        distanceR = Integer.toString((int)( calculDist(latitude, longitude)))+" Mètres";
        lat.setText("Latitude : "+Double.toString(latitude));
        lng.setText("Longitude : "+Double.toString(longitude));
        dest.setText("Distance restante : "+distanceR);
        if(destination){
            destination = false;
          //  sendMsgIntent();
        }
        if(connectedHeadphones){
            readMe.speak("Il reste "+distanceR, TextToSpeech.QUEUE_FLUSH,null);
        }

        Log.d("__latitude : ",""+latitude);
        Log.d("__longitude : ",""+longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public double calculDist(double lat, double lng){
        double delta_Lat = (lat - lat_Tacos) * 40000000/360;
        double delta_Lng = (lng - lng_Tacos) * (40000000*0.67)/360;
        return Math.sqrt((delta_Lat*delta_Lat)+(delta_Lng*delta_Lng));
    }

    public void sendMsgIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Bravo !");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "AZERTYUIOP !!!");
        startActivity(shareIntent);
    }

    public class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                return;
            }

            connectedHeadphones = (intent.getIntExtra("state", 0) == 1);
            boolean connectedMicrophone = (intent.getIntExtra("microphone", 0) == 1) && connectedHeadphones;
            String headsetName = intent.getStringExtra("name");
            Log.d("__headphones","headphones connected"+connectedHeadphones);
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, 666);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 666: {
                if (data == null) break;
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                text = result.get(0);
                Log.d("result","result = "+text);
                if ((resultCode == RESULT_OK && null != data) && text.equalsIgnoreCase("distance")) {
                    if(compteur < limite){

                        lat.setText("Latitude: "+ latitude);
                        lng.setText("Longitude: " + longitude);
                        dest.setText("Distance : " + distanceR);

                        //if(connectedHeadphones) {
                        readMe.speak(""+distanceR, TextToSpeech.QUEUE_FLUSH,null);
                        //}
                        compteur++;
                    }
                    else{
                        startActivity(intentPong);
                    }
                }
                else{
                    readMe.speak("wazaaaaaaaaa", TextToSpeech.QUEUE_FLUSH,null);
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (headsetPlugReceiver != null) {
            unregisterReceiver(headsetPlugReceiver);
            headsetPlugReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.requestGPSLocation();
                } else {
                    Toast.makeText(this, "With out this permission it might don't work, sorry bro", Toast.LENGTH_SHORT).show();
                    this.isGPSEnabled = false;
                }
                break;
        }
    }
}
