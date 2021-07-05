package com.example.tank1;

public class Projectile extends Unit {

//    public static final int DESTORYED = 0;

    public Projectile(int owner, int x, int y, int direction, int pixelsPerBlock) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.owner = owner;
        speed = (int)(pixelsPerBlock / 10 * 3.5);
        isMoving = true;
    }

}