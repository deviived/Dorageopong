package com.pachecoluc.geolocaltest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Particle {

    private int x;
    private int y;
    private int vx;
    private int vy;
    private int energy;
    private int size;
    private Paint painter;

    public Particle(int x, int y, boolean left, boolean top) {
        this.x = x;
        this.y = y;
        this.vx = (int)(Math.random() * (left ? 5 : -5));
        this.vy = (int)(Math.random() * (top ? 5 : -5));

        this.size = (int)(Math.random() * 30);

        this.painter = new Paint(0);

        this.energy = 60;

        this.painter.setColor(Color.parseColor("#e69c21"));
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.x, this.y, 5, this.painter);
        //canvas.drawRect(this.x - 5, this.y - 5, this.x + 5, this.y + 5, this.painter);
    }

    public void update() {
        this.x += vx;
        this.y += vy;

        this.painter.setAlpha((this.energy) * 4);

        this.energy--;
    }


    public boolean isDead() {
        return this.energy <= 0;
    }

}
