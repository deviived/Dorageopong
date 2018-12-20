package com.pachecoluc.geolocaltest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View implements View.OnTouchListener, SensorEventListener {

    private Paint paint;
    private MyScreen myScreen = new MyScreen(100,100);
    private float doigt = 300;

    SensorManager sensorManager;

    float textSize;
    Intent gameOverIntent;
    boolean fail = false;
    float acceleration = 0;
    boolean isRunning = true;

    //SCORE
    int SCORE_ME = 0;
    int SCORE_ENNEMY = 0;

    //SOUNDS
    private MediaPlayer opening;
    SoundPool soundPool;
    private int soundID;
    boolean plays = false, loaded = false;
    AudioManager audioManager;
    Vibrator v;

    //SCREEN ELEMENTS
    private Ennemi ennemi = new Ennemi(200, 0, 2, 0);
    private Ballon ballon = new Ballon(200, 150, 10, 8, 50, Color.BLUE);

    //BITMAP
    private Bitmap nuage;
    private Bitmap boule;
    private Bitmap bouleResized;
    private Bitmap nuageResized;
    private Bitmap nuageEnnemi;
    private Bitmap nuageResizedEnnemi;

    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private String phoneNumber = null;
    private TelephonyManager telephonyManager;

    SmsManager sms;

    public GameView(Context context) {
        super(context);

        //SET RESOURCES
        paint = new Paint();
        this.setBackgroundResource(R.drawable.db_pont);

        isRunning = true;

        //CLOUDS BITMAP
        nuage = BitmapFactory.decodeResource(getResources(), R.drawable.nuagemagique);
        nuageResized = Bitmap.createScaledBitmap(nuage, 269, 128, false);
        nuageEnnemi = BitmapFactory.decodeResource(getResources(), R.drawable.nuage_ennemi);
        nuageResizedEnnemi = Bitmap.createScaledBitmap(nuageEnnemi, 269, 128, false);

        //BALL BITMAP
        boule = BitmapFactory.decodeResource(getResources(), R.drawable.bouledudragon);
        bouleResized = Bitmap.createScaledBitmap(boule, 90, 90, false);

        //SOUNDS
        opening = MediaPlayer.create(getContext(), R.raw.opening_db);
        // Load the sounds
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            }
        });
        soundID = soundPool.load(getContext(), R.raw.sound_effect_punch, 1);
        v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);

        gameOverIntent = new Intent(getContext(), GameOver.class);
        this.setOnTouchListener(this);

        this.telephonyManager = (TelephonyManager)this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        sms = SmsManager.getDefault();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //UPDATE PAINT
        paint.setTextSize(60);
        paint.setColor(Color.WHITE);

        //GET CANVAS SIZE
        myScreen.setWidth(canvas.getWidth());
        myScreen.setHeight(canvas.getHeight());

        //DRAW Ballon particle
        for (Particle particle : particles) {
            particle.draw(canvas);
        }

        //DRAW SCREEN ELEMENTS
        canvas.drawBitmap(bouleResized, ballon.getPosX() -45, ballon.getPosY() -45,paint);
        canvas.drawBitmap(nuageResized,doigt -134, myScreen.getHeight() -100, paint);
        canvas.drawBitmap(nuageResizedEnnemi, ennemi.getPosX() -134, -10, paint);
        canvas.drawText("" +SCORE_ME, 20, (myScreen.getHeight() /2), paint);
        canvas.drawText(""  +SCORE_ENNEMY, myScreen.getWidth() -60, (myScreen.getHeight()/2), paint);

        updateDraw();
    }

    public void updateDraw(){

        //CHECK COLLISIONS
        checkCollisions(ballon);
        checkCollisionEnnemy(ballon);

        //MOVEMENTS
        ballMovement(ballon);
        ballParticles();
        iA(ennemi);

        if (this.isRunning) invalidate();
    }

    public void checkCollisions(Ballon ball){
        if(ball.getPosX() < 45 || ball.getPosX() > myScreen.getWidth()-45){
            ball.setVitX(ball.getVitX()*-1);
        }
        if(ball.getPosY() < 0){
            ball.setPosY(myScreen.getHeight()/2);
            ball.setVitY(-8);
            if(ball.getVitX()<0){
                ball.setVitX(-8);
            }
            else{
                ball.setVitX(8);
            }
            acceleration = 0;
            SCORE_ME += 1;
            if(SCORE_ME == 2){
                sendSms(true);
                isRunning = false;
                gameOver();
            }
        }
        if((ball.getPosY() > myScreen.getHeight())){
            ball.setPosY(myScreen.getHeight()/2);
            ball.setVitY(8);
            if(ball.getVitX()<0){
                ball.setVitX(-8);
            }
            else{
                ball.setVitX(8);
            }
            acceleration = 0;
            SCORE_ENNEMY += 1;
            if(SCORE_ENNEMY == 2){
                sendSms(false);
                gameOver();
            }
        }
        if((Math.abs(doigt - ball.getPosX()) <= 134) && Math.abs(myScreen.getHeight()-(ball.getPosY()+45)) <= 65){
            v.vibrate(300);
            soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
            if(Math.abs(doigt - ball.getPosX()) <= 134 && Math.abs(doigt - ball.getPosX()) > 120){
                if(ball.getVitX()<0){
                    ball.setVitX(ball.getVitX()-10);
                }
                else{
                    ball.setVitX(ball.getVitX()+10);
                }
            }
            if(Math.abs(doigt - ball.getPosX()) <= 119 && Math.abs(doigt - ball.getPosX()) > 100){
                if(ball.getVitX()<0){
                    ball.setVitX(ball.getVitX()-6);
                }
                else{
                    ball.setVitX(ball.getVitX()+6);
                }
            }
            if(Math.abs(doigt - ball.getPosX()) <= 99 && Math.abs(doigt - ball.getPosX()) > 80){

            }
            if(Math.abs(doigt - ball.getPosX()) <= 79 && Math.abs(doigt - ball.getPosX()) > 60){
                if(ball.getVitX() < 0){
                    ball.setVitX(ball.getVitX() +6);
                }
                else{
                    ball.setVitX(ball.getVitX() -6);
                }
            }
            if(Math.abs(doigt - ball.getPosX()) <= 59 && Math.abs(doigt - ball.getPosX()) > 40){
                if(ball.getVitX()<0){
                    ball.setVitX(3);
                }
                else{
                    ball.setVitX(-3);
                }
            }
            if(Math.abs(doigt - ball.getPosX()) <= 39 && Math.abs(doigt - ball.getPosX()) > 20){
                if(ball.getVitX() < 0){
                    ball.setVitX(2);
                }
                else{
                    ball.setVitX(-2);
                }
            }
            if(Math.abs(doigt - ball.getPosX()) <= 19){
                if(ball.getVitX() < 0){
                    ball.setVitX(1);
                }
                else{
                    ball.setVitX(-1);
                }
            }
            acceleration *= -1;
            acceleration -= 1;
            ball.setVitY(-8 + acceleration);

        }
    }

    public void ballMovement(Ballon ball){
        ball.setPosX(ball.getPosX()+ball.getVitX());
        ball.setPosY(ball.getPosY()+ball.getVitY());
    }

    private void ballParticles() {
        for (int i = 0; i < 5; i++) {
            double angle = Math.random()* Math.PI * 2;
            int cx = (int)(Math.cos(angle) * (this.ballon.getRad() / 1.5));
            int cy = (int)(Math.sin(angle) * (this.ballon.getRad()  / 1.5));
            particles.add(new Particle(
                    (int)this.ballon.getPosX() + cx,
                    (int)this.ballon.getPosY() + cy,
                    this.ballon.getVitX() < 0,
                    this.ballon.getVitY() < 0
            ));
        }


        for (int i = 0; i < particles.size(); i++)
        {
            particles.get(i).update();
            if (particles.get(i).isDead()) {
                particles.remove(i);
            }
        }
    }

    public void iA(Ennemi enemy){
        float middleH = myScreen.getHeight() /2;
        float middleW = myScreen.getWidth() /2;
        if(ballon.getPosY() < middleH) {
            if(enemy.getPosX() < ballon.getPosX()) {
                enemy.setVitX(10);
            } else {
                enemy.setVitX(-8);
            }
        } else {
            if(enemy.getPosX() < middleW -150){
                enemy.setVitX(11);
            }
            if(enemy.getPosX() > middleW +150){
                enemy.setVitX(-7);
            }
        }
        enemy.setPosX(enemy.getPosX()+enemy.getVitX());
    }

    public void checkCollisionEnnemy(Ballon ball) {
        if((Math.abs(ennemi.getPosX() - ball.getPosX()) <= 134) && Math.abs(0-ball.getPosY()) <= 65){
            v.vibrate(300   );
            soundPool.play(soundID, 1.0F, 1.0f, 1, 0, 1.0f);
            acceleration *= -1;
            acceleration += 1;
            ball.setVitY(8 + acceleration);
        }
    }

    public void gameOver(){
        opening.stop();
        getContext().startActivity(gameOverIntent);
        return;
    }

    public void sendSms(boolean win){
        String msg;
        if(win){
            msg = "You win!";
        }else{
            msg = "You lose!";
        }
        try {
            String phonenumber = this.telephonyManager.getLine1Number();
            if (phonenumber != null && !phonenumber.equals("")) {
                    sms.sendTextMessage(phonenumber, null, "You lose!", null, null);
            }
        } catch (SecurityException e) {

        }
    }

    public boolean onTouch(View v, MotionEvent m){
        //GET TOUCH XPOS
        doigt = m.getX();
        return true;
    };

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            // Movement
            float x = values[0];
            //Log.e("testAcc", "value : "+x);

            if((x < -1) && ((doigt + 134) < myScreen.getWidth())) {
                doigt+=8;
            } else if ((x > 1) && ((doigt-134) >= 0)) {
                doigt -=8;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onVisibilityChanged(View GameView, int visibility) {
        super.onVisibilityChanged(GameView, visibility);
        if (visibility == View.VISIBLE) {
            //onResume called }
            opening.start();
        } else {
            opening.pause();
        }
    }

}
