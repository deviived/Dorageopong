package com.pachecoluc.geolocaltest;

public class Ennemi {
    private float posX;
    private float posY;
    private float vitX;
    private float vitY;

    public Ennemi(float posX, float posY, float vitX, float vitY){
        this.posX = posX;
        this.posY = posY;
        this.vitX = vitX;
        this.vitY = vitY;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getVitX() {
        return vitX;
    }

    public void setVitX(float vitX) {
        this.vitX = vitX;
    }

    public float getVitY() {
        return vitY;
    }

    public void setVitY(float vitY) {
        this.vitY = vitY;
    }
}
