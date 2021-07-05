package com.example.tank1;

import android.graphics.Rect;
import android.util.Log;

public class Tank extends Unit {
    public static final int DESTORYED = 0;
    public static final int ACTIVE = 1;

    int type;
    int hitPoint;
    int attackInterval;
    int state;

    /*
     * pixelPerBlock: to calculate how many pixels it should move
     */
    public Tank(int type, int x, int y, int direction, int owner, int pixelPerBlock) {
        this.type = type;
        int[] stats = Constants.TYPES[type]; // stats:[HP, speed scalar, attack interval]
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.owner = owner;
        hitPoint = stats[0];
        // pixelPerBlock / 10 is a proper base speed. Change speed by multiplying a scalar.
        speed = (int)((pixelPerBlock / 10) * (1+0.25*stats[1]));
        Log.d("speed of tank type" + this.type, "" + speed);
        isMoving = false;
        attackInterval = stats[2];
        state = ACTIVE;
    }

    public void hit() {
        if(hitPoint > 0)
            hitPoint--;
        else
            state = DESTORYED;
    }

    public void setHitPoint(int hitPoint) {
        this.hitPoint = hitPoint;
        state = ACTIVE;
    }

    public int getHitPoint() {
        return hitPoint;
    }

    public int getAttackInterval() {
        return attackInterval;
    }

    public int getState() {
        return state;
    }



}