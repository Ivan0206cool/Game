package com.example.tank1;

import android.graphics.Rect;

public class Brick extends Unit{

    public static final int DESTORYED = 0;
    int hitPoint=1;
    int state=1;
    int width;
    int height;
    public Brick(int owner,int x, int y) {
        this.x = x;
        this.y = y;
        this.owner = owner;

    }

    public void hit() {
        if(hitPoint > 0)
            hitPoint--;
        else
            state = DESTORYED;
    }

    public Rect getBrickShape () {
        return new Rect(x, y, x + width, y + height);
    }

}
