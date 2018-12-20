package com.pachecoluc.geolocaltest;

import android.graphics.Paint;

public class Ballon {
    private float xPos;
    private float yPos;
    private float xVit;
    private float yVit;
    private float radius;
    private Paint paint = new Paint();

    public Ballon(float posX, float posY, float vitX, float vitY, float rad, int color){
        this.xPos = posX;
        this.yPos = posY;
        this.xVit = vitX;
        this.yVit = vitY;
        this.radius = rad;
        this.paint.setColor(color);
    }

    public void setPosX(float x){
        this.xPos = x;
    }

    public void setPosY(float y){
        this.yPos = y;
    }

    public float getPosX(){
        return this.xPos;
    }

    public float getPosY(){
        return this.yPos;
    }

    public float getVitX(){
        return this.xVit;
    }

    public float getVitY(){
        return this.yVit;
    }

    public void setVitX(float vitX){
        this.xVit = vitX;
    }

    public void setVitY(float vitY){
        this.yVit = vitY;
    }

    public float getRad(){
        return this.radius;
    }

    public Paint getColor(){
        return this.paint;
    }
}
