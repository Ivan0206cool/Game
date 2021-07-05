package com.example.tank1;

public class Unit {
    int speed; // pixels moved per tick. one sec. = 60 tick
    int direction; //0=up, 1=right, 2=down, 3=left
    // x,y are the center coordinates of model
    int x;
    int y;
    boolean isMoving;
    int owner;

    // only move towards current direction
    public void move() {
        switch (direction) {
            case 0:
                y -= speed;
                break;
            case 1:
                x += speed;
                break;
            case 2:
                y += speed;
                break;
            case 3:
                x -= speed;
                break;
            default:
                break;
        }

    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return  x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setOffset(int x, int y) {
        this.x += x;
        this.y += y;
    }


    public int getY() {
        return y;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setMovingState(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public boolean getMovingState() {
        return isMoving;
    }

    public int getOwner() {
        return owner;
    }
}