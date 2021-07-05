package com.example.tank1;

import android.util.Log;

import java.util.Random;

public class EnemyAI {
    //private int callCounter;

    Tank tank;
    Tank target;
    GameViewNew gameView;
    int curX;
    int curY;
    int attackCooldown; //Slow enemy attack down. In ticks
    int moveLag; //Slow enemy movement down. How many ticks the enemy should wait per one second(60 ticks)
    int bias;
    int difficulty;
    boolean xAligned;
    boolean yAligned;
    Random rand;

    public EnemyAI(Tank t, Tank target, GameViewNew view, int difficulty) {
        tank = t;
        this.target = target;
        gameView = view;
        curX = t.getX();
        curY = t.getY();
        rand =  new Random();
        bias = gameView.getPlayAreaBlockInPixel() / 2;
        this.difficulty = difficulty;
        attackCooldown = (4-difficulty) * tank.getAttackInterval();
        moveLag = 12 - difficulty * 3;
    }

    // direction: 0=up, 1=right, 2=down, 3=left
    public void align() {
        int targetX = target.getX();
        int targetY = target.getY();

        curX = tank.getX();
        curY = tank.getY();

        //Log.d("EnemyAI", "target x, y : " + targetX + ", " + targetY);
        //Log.d("EnemyAI", "cur x, y : " + curX + ", " + curY);

        int distanceX = curX - targetX; // If distanceX > 0, target is to our left
        int distanceY = curY - targetY; // If distanceY > 0, target is above us

        // check if it's aligned
        if (Math.abs(curX - targetX) <= bias) {
            xAligned = true;
        } else {
            xAligned = false;
        }

        if (Math.abs(curY - targetY) <= bias) {
            yAligned = true;
        } else {
            yAligned = false;
        }

        if (xAligned && !yAligned) {
            if ((curY - targetY ) > 0)
                tank.setDirection(0);
            else
                tank.setDirection(2);
        } else if (yAligned) {
            if ((curX - targetX) > 0)
                tank.setDirection(3);
            else
                tank.setDirection(1);
        } else {
            // not aligned, get move
            int dir = 0;

            if(distanceX < distanceY) { //align X
                dir = (distanceX > 0) ? 3 : 1;
            }else { //align Y
                dir = (distanceY > 0) ? 0 : 2;
            }
            tank.setDirection(dir);

            // boundaryDetect
            if(curX < (gameView.getPlayAreaLeft() + gameView.getPlayAreaBlockInPixel())
                    || curX > (gameView.getPlayAreaRight() - gameView.getPlayAreaBlockInPixel())) {

                tank.setDirection(dir);
                //Log.d("EnemyAI", "LR boundary reached");

            } else if(curY < (gameView.getPlayAreaTop() + gameView.getPlayAreaBlockInPixel())
                    || curY > (gameView.getPlayAreaBottom() - gameView.getPlayAreaBlockInPixel())) {

                tank.setDirection(dir);
                Log.d("EnemyAI", "TB boundary reached");
            }
            //Log.d("EnemyAI", "final direction : " + tank.getDirection());
            int r = rand.nextInt(60) + 1;
            if(r > moveLag)
                tank.move();
        }
        //Log.d("EnemyAI", "align : " + xAligned + ", " + yAligned);
    }

    public void dodge(Projectile p) {

    }

    public void attack() {
        if(attackCooldown == 0) {
            if(xAligned || yAligned) {
                gameView.fire(tank);
                attackCooldown = (4-difficulty) * tank.getAttackInterval();
            } else {
                if(rand.nextInt(100) < 20) {
                    gameView.fire(tank);
                    attackCooldown = (4-difficulty) * tank.getAttackInterval();
                }
            }
        } else {
            attackCooldown--;
        }


    }

    public void pathFinding(int x, int y) {

    }

}