package com.example.tank1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;


public class GameViewNew extends SurfaceView implements SurfaceHolder.Callback{
    public static final String RESUMED = "com.example.tank1.resumed";
    public final static boolean STOP = false;
    public final static boolean MOVING = true;
    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;
    //static number of the owner
    public final static int PLAYER1 = 100;

    public final static int ENEMY = 102;




    private String fpsText = "0.0";
    private Context mContext;
    private SurfaceHolder holder;
    private LoopThread loopThread;
    private Canvas c;
    private Paint paint;
    private boolean deadstate= false ;

    Handler handler =new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            AlertDialog.Builder alterDialog = new AlertDialog.Builder(mContext);
            alterDialog.setTitle("玩家死亡")
                    .setMessage("Loser~")
                    .setPositiveButton("重新遊戲", (dialog, which) ->
                            {

                                ((MainActivity)mContext).reStarGame();

//                                setDeadstate(false);
                            }
                    )
                    .setNegativeButton("離開遊戲", (dialog, which) -> {
                        ((MainActivity) mContext).finish();
                    });
            alterDialog.show();
            super.handleMessage(msg);
        }
    };

    Handler handler1 =new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            AlertDialog.Builder alterDialog = new AlertDialog.Builder(mContext);
            alterDialog.setTitle("玩家獲勝")
                    .setMessage("Winner~")
                    .setPositiveButton("重新遊戲", (dialog, which) ->
                            {

                                ((MainActivity)mContext).reStarGame();

//                                setDeadstate(false);
                            }
                    )
                    .setNegativeButton("離開遊戲", (dialog, which) -> {
                        ((MainActivity) mContext).finish();
                    });
            alterDialog.show();
            super.handleMessage(msg);
        }
    };

    //game states
    private boolean playing = false;
    private boolean finished = false;

    // data of the play area
    private int playAreaBlockInPixel; // 50 for 1440P
    private int playAreaBlockWidth = 32;
    private int playAreaBlockHight = 24; // a 32*24 map

    //screen data
    private int screenRatio = 16; //16=16:9, 18=18:9  etc.
    private int screenWidth;
    private int screenHeight;
    private int pixelPerBlock; //block size in pixels, 160 for 1440P

    private int playAreaLeft;
    private int playAreaRight;
    private int playAreaTop;
    private int playAreaBottom;

    // other measurements
    private int projctileSize;
    private int tankSize;
    private int difficulty;

    // components and units
    private Rect leftUIRect;
    private Rect rightUIRect;
    private Tank player;
    private Brick brick;
    private ArrayList<Tank> tankList = new ArrayList<Tank>();
    private ArrayList<Projectile> projectileList = new ArrayList<Projectile>();
    private Iterator<Projectile> projIter;

    private ArrayList<Brick> BrickList=new ArrayList<>();
    private Iterator<Brick> bricker;

    private ArrayList<Brick> SolidList = new ArrayList<>();
    private Iterator<Brick> solider;

    private ArrayList<Brick> flowerList = new ArrayList<>();
    private Iterator<Brick> flower1;

    private int attackCooldown;

    private int enemyRespawnX;
    private int enemyRespawnY;

    private int playerRespawnX;
    private int playerRespawnY;

    EnemyAI enemyAI;


    //for test
    private int enemyRespawnTime;
    private  int playerRespawnTime=120;
    private int j=0;
    private int lose=0;
    private int win=0;
    private int count1=5;
    private int count2=1;

    //resources
    Bitmap[] tank1; // player's tank pic
    Bitmap[] tank2; // enemy's tank pic
    Bitmap brick1;
    Bitmap brick2;
    Bitmap proj1;
    Bitmap flower;
//    Bitmap proj2;
    int[] tankDrawable1 = {
            R.drawable.tank1_up,  R.drawable.tank1_right,  R.drawable.tank1_down, R.drawable.tank1_left};
    int[] tankDrawable2 = {
            R.drawable.tank2_up,  R.drawable.tank2_right,  R.drawable.tank2_down, R.drawable.tank2_left};


    public GameViewNew(Context context) {
        super(context);
        mContext = context;
        holder = this.getHolder();
        holder.addCallback(this);
        paint = new Paint();

        // loading resources
        tank1 = new Bitmap[4];
        tank2 = new Bitmap[4];
        for(int i=0; i<4; i++) {
            Bitmap pic1 = BitmapFactory.decodeResource(getResources(), tankDrawable1[i]);
            Bitmap pic2 = BitmapFactory.decodeResource(getResources(), tankDrawable2[i]);
            tank1[i] = pic1;
            tank2[i] = pic2;
        }

        proj1 = BitmapFactory.decodeResource(getResources(), R.drawable.projctile);
        brick1=BitmapFactory.decodeResource(getResources(), R.drawable.break_brick);
        brick2=BitmapFactory.decodeResource(getResources(),R.drawable.solid_brick);
        flower=BitmapFactory.decodeResource(getResources(),R.drawable.flower);
    }



    //getters and setters
    public int getPlayAreaLeft() {
        return playAreaLeft;
    }

    public int getPlayAreaRight() {
        return playAreaRight;
    }

    public int getPlayAreaTop() {
        return playAreaTop;
    }

    public int getPlayAreaBottom() {
        return playAreaBottom;
    }

    public int getPlayAreaBlockInPixel() {
        return playAreaBlockInPixel;
    }


    @Override
    public void draw(Canvas canvas) {
        if(canvas != null) {
            super.draw(canvas);
            drawBackground(canvas, paint);
            drawTanks(canvas, paint);
            drawProjectile(canvas, paint);
            drawFps(canvas, paint);
            drawBrick(canvas,paint);
            drawSolid(canvas,paint);
            drawpoint(canvas,paint);
            drawwin(canvas,paint);
            drawlose(canvas,paint);
            drawlife(canvas,paint);
            drawvic(canvas,paint);
            drawflower(canvas,paint);
        }
    }

    public void setFps(double fps) {
        fpsText = new Formatter().format("%.1f", fps).toString();
    }

    //a sort of draw methods
    private void drawFps(Canvas c, Paint p) {
        p.setColor(Color.RED);
        p.setTextSize(48);
        c.drawText("FPS: " + fpsText, 20, 40, p);
    }

    private void drawpoint(Canvas c, Paint p) {
        p.setColor(Color.BLUE);
        p.setTextSize(48);
        c.drawText("擊中磚塊數: " + j, 20, 100, p);
    }

    private void drawwin(Canvas c, Paint p) {
        p.setColor(Color.rgb(255,255,255));
        p.setTextSize(48);
        c.drawText("敵人生命值: " + count1, 20, 160, p);
    }

    private void drawlife(Canvas c, Paint p) {
        p.setColor(Color.YELLOW);
        p.setTextSize(48);
        c.drawText("我方生命值: " + count2, 20, 220, p);
    }

    private void drawlose(Canvas c, Paint p) {
        p.setColor(Color.GREEN);
        p.setTextSize(48);
        c.drawText("Lose: " + lose, 20, 280, p);
    }

    private void drawvic(Canvas c, Paint p) {
        p.setColor(Color.rgb(255,248,220));
        p.setTextSize(48);
        c.drawText("Win: " + win, 20, 340, p);
    }


    private void drawBrick(Canvas c,Paint p) {
        bricker = BrickList.iterator();
        while (bricker.hasNext()) {
            Brick t = bricker.next();
            int left = t.getX() - 2* playAreaBlockInPixel;
            int right = t.getX() +  2*playAreaBlockInPixel;
            int top = t.getY() -  2*playAreaBlockInPixel;
            int bottom = t.getY() +2* playAreaBlockInPixel;
            Rect brickRect = new Rect(left, top, right, bottom);
            c.drawBitmap(brick1, null, brickRect, p);

        }
    }

    //灰磚塊繪圖
    private void drawSolid(Canvas c,Paint p) {
        solider = SolidList.iterator();
        while (solider.hasNext()) {
            Brick t = solider.next();
            int left = t.getX() - 2* playAreaBlockInPixel;
            int right = t.getX() +  2*playAreaBlockInPixel;
            int top = t.getY() -  2*playAreaBlockInPixel;
            int bottom = t.getY() +2* playAreaBlockInPixel;
            Rect brickRect = new Rect(left, top, right, bottom);
            c.drawBitmap(brick2, null, brickRect, p);

        }
    }

    //花磚塊繪圖
    private void drawflower(Canvas c,Paint p) {
        flower1 = flowerList.iterator();
        while (flower1.hasNext()) {
            Brick t = flower1.next();
            int left = t.getX() - 2* playAreaBlockInPixel;
            int right = t.getX() +  2*playAreaBlockInPixel;
            int top = t.getY() -  2*playAreaBlockInPixel;
            int bottom = t.getY() +2* playAreaBlockInPixel;
            Rect brickRect = new Rect(left, top, right, bottom);
            c.drawBitmap(flower, null, brickRect, p);

        }
    }

    private void drawBackground(Canvas c, Paint p) {
        //first the main background
        p.setColor(Color.rgb(100,200,190));
        c.drawRect(leftUIRect, p);
        c.drawRect(rightUIRect, p);
        // leave the play area at this moment, cuz it's black
        // we don't need to draw black
    }

    private void drawTanks(Canvas c, Paint p) {
        for(Tank t : tankList) {
            if(t.getHitPoint() > 0) {
                int direction = t.getDirection();
                int left = t.getX() - playAreaBlockInPixel;
                int right = t.getX() + playAreaBlockInPixel;
                int top = t.getY() - playAreaBlockInPixel;
                int bottom = t.getY() + playAreaBlockInPixel;
                Rect tankRect = new Rect(left, top, right, bottom);

                    if (t.getOwner() == PLAYER1)
                        c.drawBitmap(tank1[direction], null, tankRect, null);
                    else if (t.getOwner() == ENEMY)
                        c.drawBitmap(tank2[direction], null, tankRect, null);

            }
        }
    }

    private void drawProjectile(Canvas c, Paint p) {
        // we use iterator approach
        projIter = projectileList.iterator();
        while(projIter.hasNext()) {
            Projectile t = projIter.next();
            int left = t.getX() - projctileSize;
            int right = t.getX() + projctileSize;
            int top = t.getY() - projctileSize;
            int bottom = t.getY() + projctileSize;
            Rect projRect = new Rect(left, top, right, bottom);
            c.drawBitmap(proj1, null, projRect, null);
        }

    }
    //draw methods end

    public int getCount1(){
        return count1;
    }

    public void dead()
    {
        handler.sendMessage(handler.obtainMessage());

    }

    public void win(){
        handler1.sendMessage(handler1.obtainMessage());
    }
    public void setDeadstate(boolean dead)
    {
        deadstate = dead;

    }
    public boolean getDeadstate()
    {
        return deadstate;
    }

    public boolean hitDetectBase1(Projectile p,Brick b){

        int pleft = p.getX() - projctileSize ;
        int pright = p.getX() + projctileSize ;
        int pup = p.getY() - projctileSize;
        int pdown = p.getY() + projctileSize;

        int bleft = b.getX() - tankSize*2;
        int bright = b.getX() + tankSize *2;
        int bup = b.getY() - tankSize *2;
        int bdown = b.getY() + tankSize * 2;

        Rect po = new Rect(pleft, pup, pright, pdown);
        Rect br = new Rect(bleft, bup, bright, bdown);
        int w = p.getDirection();

        boolean id = false;
        switch (w) {
            //up
            case 0:
                if (po.intersect(br)) {
                    if (pup <= bdown)
                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;
            //right
            case 1:
                if (po.intersect(br)) {
                    if (pright >= bleft)
                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;
            //down
            case 2:
                if (po.intersect(br)) {
                    if (pdown >= bup)
                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;
            //left
            case 3:
                if (po.intersect(br)) {
                    if (pleft <= bright)
                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;


        }


        return id;
//        int pX = p.getX();
//        int pY = p.getY();
//        int bX = b.getX();
//        int bY = b.getY();
//        if(pX - projctileSize >= bX - tankSize && pX + projctileSize <= bX + tankSize) {
//            if(pY - projctileSize >= bY - tankSize && pY + projctileSize <= bY + tankSize) {
////
//                return true;
//            }
//            else
//                return false;
//        } else {
//            return false;
//        }
    }

    public boolean hitDetectBase(Projectile p, Tank t) {



        int pleft = p.getX() - projctileSize ;
        int pright = p.getX() + projctileSize ;
        int pup = p.getY() - projctileSize ;
        int pdown = p.getY() + projctileSize ;

        int tleft = t.getX() - tankSize * 2;
        int tright = t.getX() + tankSize * 2;
        int tup = t.getY() - tankSize * 2;
        int tdown = t.getY() + tankSize * 2;

        Rect po = new Rect(pleft, pup, pright, pdown);
        Rect br = new Rect(tleft, tup, tright, tdown);
        int w = p.getDirection();

        boolean id = false;
        switch (w) {
            //up
            case 0:
                if (po.intersect(br)) {
                    if (pup <= tdown && p.getOwner() != t.getOwner()) {
                        Log.d("錯誤訊息", Integer.toString(p.getOwner())+" "+Integer.toString(t.getOwner()));
                        id = true;
                    }
                    else
                        id = false;
                } else
                    id = false;
                break;
            //right
            case 1:
                if (po.intersect(br)) {
                    if (pright >= tleft && p.getOwner() != t.getOwner() )

                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;
            //down
            case 2:
                if (po.intersect(br) ) {
                    if (pdown >= tup && p.getOwner() != t.getOwner())
                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;
            //left
            case 3:
                if (po.intersect(br) ) {
                    if (pleft <= tright && p.getOwner() != t.getOwner())
                        id = true;
                    else
                        id = false;
                } else
                    id = false;
                break;
        }
        return id;

//        if(pX - projctileSize >= tX - tankSize && pX + projctileSize <= tX + tankSize) {
//            if(pY - projctileSize >= tY - tankSize && pY + projctileSize <= tY + tankSize) {
//                if(p.getOwner() != t.getOwner()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//            else
//                return false;
//        } else {
//            return false;
//        }
    }



    public int collisionDetect(Brick b) {
        Tank t=tankList.get(0);

        int left = b.getX() - 2*playAreaBlockInPixel;
        int right = b.getX() + 2*playAreaBlockInPixel;
        int top = b.getY() - 2*playAreaBlockInPixel;
        int bottom = b.getY() + 2*playAreaBlockInPixel;
        int tleft = t.getX() - playAreaBlockInPixel;
        int tright = t.getX() + playAreaBlockInPixel;
        int ttop = t.getY() - playAreaBlockInPixel;
        int tbottom = t.getY() + playAreaBlockInPixel;

            Rect tankrect=new Rect(tleft,ttop,tright,tbottom);

            if(tankrect.intersects(left,top,right,bottom))
                return 1;
            else
                return  0;



    }

    public int collisiondect2(Brick b){
        Tank t1=tankList.get(1);
        int left = b.getX() - 2*playAreaBlockInPixel;
        int right = b.getX() + 2*playAreaBlockInPixel;
        int top = b.getY() - 2*playAreaBlockInPixel;
        int bottom = b.getY() + 2*playAreaBlockInPixel;
        int t1left = t1.getX() - playAreaBlockInPixel;
        int t1right = t1.getX() + playAreaBlockInPixel;
        int t1top = t1.getY() - playAreaBlockInPixel;
        int t1bottom = t1.getY() + playAreaBlockInPixel;
        Rect enemyrect=new Rect(t1left,t1top,t1right,t1bottom);
        if(enemyrect.intersects(left,top,right,bottom))
            return 1;
        else
            return  0;

    }

    public int boundaryDetect(Tank t) {
        if(t.getX() < playAreaLeft + playAreaBlockInPixel) {
            t.setOffset(1, 0);
            return 1; // reach left
        }

        else if(t.getX() > playAreaRight - playAreaBlockInPixel) {
            t.setOffset(-1, 0);
            return 2; // reach right
        }
        else if(t.getY() < playAreaTop + playAreaBlockInPixel) {
            t.setOffset(0, 1);
            return 3; // reach top
        }
        else if(t.getY() > playAreaBottom - playAreaBlockInPixel) {
            t.setOffset(0, -1);
            return 4; // reach bottom
        }
        else
            return 0;
    }

    public boolean boundaryDetect(Projectile p) {
        if(p.getX() < playAreaLeft + projctileSize
                || p.getX() > playAreaRight - projctileSize
                || p.getY() < playAreaTop
                || p.getY() > playAreaBottom) {
            return true;
        } else {
            return false;
        }
    }

    public int collision ()
    {
        Tank t=tankList.get(0);
        Tank t1=tankList.get(1);

        int tleft = t.getX() - 2*playAreaBlockInPixel;
        int tright = t.getX() + 2*playAreaBlockInPixel;
        int ttop = t.getY() - 2*playAreaBlockInPixel;
        int tbottom = t.getY() + 2*playAreaBlockInPixel;
        int left = t1.getX() - 2*playAreaBlockInPixel;
        int right = t1.getX() +2* playAreaBlockInPixel;
        int top = t1.getY() - 2*playAreaBlockInPixel;
        int bottom = t1.getY() +2* playAreaBlockInPixel;


        Rect tankrect=new Rect(tleft,ttop,tright,tbottom);

        if(tankrect.intersects(left,top,right,bottom))
            return 1;
        else
            return  0;

    }//敵人碰撞(我方坦克)
    public void collisionEnemyRen(Tank t1, Tank t2) {

        t1 = tankList.get(0);
        t2 = tankList.get(1);

        int left = t2.getX() - playAreaBlockInPixel-10;
        int right = t2.getX() +playAreaBlockInPixel+10 ;
        int top = t2.getY() - playAreaBlockInPixel-10 ;
        int bottom = t2.getY() +playAreaBlockInPixel+10 ;

        int tleft = t1.getX() - playAreaBlockInPixel-10;
        int tright = t1.getX() +playAreaBlockInPixel+10;
        int ttop = t1.getY() - playAreaBlockInPixel-10 ;
        int tbottom = t1.getY() + playAreaBlockInPixel+10;

        int p = t1.getDirection();
        switch (p) {

            case 0:
                if (ttop < bottom) {
                    t1.setOffset(0, 10);
                }
                break;

            case 1:
                if (tright > left) {
                    t1.setOffset(-10, 0);
                }
                break;

            case 2:
                if (tbottom > top) {
                    t1.setOffset(0, -10);
                }
                break;

            case 3:
                if (tleft < right) {
                    t1.setOffset(10, 0);
                }
                break;
        }
        Log.d("position", " " + left + " " + right + " " + top + " " + bottom + " ");
        Log.d("position", " " + tleft + " " + tright + " " + ttop + " " + tbottom + " ");
    }

    //我方碰撞(敵方坦克)
    public void  collisionEnemyRen2 (Tank t1 , Tank t2)
    {

        t1=tankList.get(0);
        t2=tankList.get(1);

        int left = t2.getX() - playAreaBlockInPixel-10;
        int right = t2.getX() + playAreaBlockInPixel+10;
        int top = t2.getY() - playAreaBlockInPixel-10;
        int bottom = t2.getY() +playAreaBlockInPixel+10;

        int tleft = t1.getX() - playAreaBlockInPixel-10;
        int tright = t1.getX() +playAreaBlockInPixel+10;
        int ttop = t1.getY() - playAreaBlockInPixel-10;
        int tbottom = t1.getY() + playAreaBlockInPixel+10;

        int p = t2.getDirection();
        switch (p){

            case 0:
                if(top<tbottom)
                {
                    t2.setOffset(0,1);
                }
                break;

            case 1:
                if (right > tleft) {
                    t2.setOffset(-1, 0);
                }
                break;

            case 2:
                if(bottom>ttop)
                {
                    t2.setOffset(0,-1);
                }
                break;

            case 3:
                if(left<tright) {
                    t2.setOffset(1,0);
                }
                break;
        }
        Log.d("position"," "+left+" "+right+" "+top+" "+bottom+" ");
        Log.d("position"," "+tleft+" "+tright+" "+ttop+" "+tbottom+" ");
    }



    public void  collision (Tank t,int i)
    {
        int d=t.getDirection();

        switch (d){
            case 0:
                    t.setOffset(0,10+i);
                    break;

            case 1:
                    t.setOffset(-(10+i),0);
                    break;

            case 2:

                    t.setOffset(0,-(10+i));
                    break;

            case 3:

                    t.setOffset(10+i,0);
                    break;

        }

    }




    int n=0;
    // game logic updates here
    public synchronized void updateStates() {
        n++;
        attackCooldown--;
        if(attackCooldown < 0)
            attackCooldown =0;



        Tank player = tankList.get(0);
        Tank enemy=tankList.get(1);



        if(player.isMoving && boundaryDetect(player) == 0 )
            player.move();

        bricker = BrickList.iterator();
        while (bricker.hasNext()) {
            Brick b = bricker.next();
            if ( collisionDetect(b) != 0){
//                player.isMoving=false;
                collision(player,0);
            }

            if(collisiondect2(b)!=0)
                collision(enemy,0);

        }

        bricker = SolidList.iterator();
        while (bricker.hasNext()) {
            Brick b = bricker.next();
            if ( collisionDetect(b) != 0){
//                player.isMoving=false;
                collision(player,0);
            }

            if(collisiondect2(b)!=0)
                collision(enemy,0);

        }

        bricker = flowerList.iterator();
        while (bricker.hasNext()) {
            Brick b = bricker.next();
            if ( collisionDetect(b) != 0){
//                player.isMoving=false;
                collision(player,0);
            }

            if(collisiondect2(b)!=0)
                collision(enemy,0);

        }

        if(collision()!=0){
//            collisionEnemyRen(player,enemy);
            collisionEnemyRen2(player,enemy);
        }
//            player.isMoving=false;

        if(count2==0 && lose==2){
            pause();
            dead();
//            if(player.getHitPoint()>0){
////
//            }



        }

//        if(enemy.getHitPoint()==0) {
//            enemyRespawnTime--;
//
//            if (enemyRespawnTime == 0) {
//
//                enemy.setHitPoint(5);
//                enemy.setX(enemyRespawnX);
//                enemy.setY(enemyRespawnY);
//                enemyRespawnTime = 50;
//                deadstate = true;
//
//            }
//        }



//            enemyRespawnTime--;
            if(count1== 0 && win==2) {
//                enemy.setHitPoint(1);
                pause();
                win();
//
            }
         else {
            enemyAI.align();
            enemyAI.attack();
        }

        if(count1==0){
            if(count1==0){
                win+=1;
                count1=5;
            }

                enemy.setHitPoint(5);
                enemy.setX(enemyRespawnX);
                enemy.setY(enemyRespawnY);

        }

        if(count1>0)
            enemy.setHitPoint(count1);


        if(count2==0){
             if(count2==0){
                 lose+=1;
                 count2=1;
             }


                 player.setHitPoint(1);
                 player.setX(screenWidth / 2);
                 player.setY(screenHeight / 2);

//             count1=5;

         }

         if(count2>0)
             player.setHitPoint(count2);

        projIter = projectileList.iterator();
        while(projIter.hasNext()) {
            Projectile p = projIter.next();
            int [] brickArrayX={300,400,500,600,700,800,900,200,200,200,
                    200,200,200,200,200,200,200,200,200,200,
                    200,200,200,200,200,200,
                    900,900,900,900,900,900,900,900,900,
                    300,400,500,600,700,800,
                    700,700,700,700,800,900,
                    200,200,200,200,300,300,300



            };
            for(int i = 0; i< Arrays.stream(brickArrayX).count(); i++)
            {
                 Brick b = BrickList.get(i);
                if (hitDetectBase1(p, b)) {
                    b.hit();
                    if (b.state == b.DESTORYED) {
                        b.setX(0);
                        b.setY(0);
                        j++;

                    }
                    projIter.remove();
                }
            }

            int [] solidArrayX={300,300,300,300,400,500,600,600,700,800,900,700,800,900,900,400,500,600,800
                    ,900

            };

            for(int i = 0; i< Arrays.stream(solidArrayX).count(); i++)
            {
                Brick b = SolidList.get(i);
                if (hitDetectBase1(p, b)) {
                    projIter.remove();
                }
            }

            int[] flowerArrayX={600,800,800,900,900,400,500,600};
            for(int i = 0; i< Arrays.stream(flowerArrayX).count(); i++)
            {
                Brick b = flowerList.get(i);
                if (hitDetectBase1(p, b)) {
                    b.hit();
                    if (b.state == b.DESTORYED) {
                        b.setX(0);
                        b.setY(0);
                        int n=(int)(Math.random()*2+1);
                        if(n==1)
                            count2++;
                        else
                            count1+=2;

                    }
                    projIter.remove();
                }
            }




            if(hitDetectBase(p, enemy)) {
                enemy.hit();
                count1--;
//                if(enemy.state == enemy.DESTORYED) {
//
//                    enemy.setX(screenWidth/2);
//                    enemy.setY(screenHeight/5);
//                    enemyRespawnTime=50;
//
//
//                }
                projIter.remove();
            }else if(hitDetectBase(p, player)){
                player.hit();
                count2--;
//                if(player.state == player.DESTORYED ) {
//
//                    player.setX(screenWidth/2);
//                    player.setY(screenHeight/2);
//
//                }
                projIter.remove();
            }else if(boundaryDetect(p)) {
                projIter.remove();
            }else {
                p.move();
            }


        }






    }

    // methods for controls
    public void movePlayer(int dir, boolean isMoving) {
        Tank player = tankList.get(0);
        // int currentDirection = player.getDirection();
        player.setDirection(dir);
        player.setMovingState(isMoving);
    }

    public synchronized void fire() {
        if(attackCooldown > 0) {
            Log.d("fire", "reloading!");
        } else {
            Tank player = tankList.get(0);
            int direction = player.getDirection();
            int offsetX = 0;
            int offsetY = 0;
            switch (direction) {
                case 0:
                    offsetY = -playAreaBlockInPixel;
                    break;
                case 1:
                    offsetX = playAreaBlockInPixel;
                    break;
                case 2:
                    offsetY = playAreaBlockInPixel;
                    break;
                case 3:
                    offsetX = -playAreaBlockInPixel;
                    break;
                default:
                    break;
            }
            Projectile p = new Projectile(
                    100, player.getX() + offsetX, player.getY() + offsetY, player.getDirection(), playAreaBlockInPixel);
            projectileList.add(p);

//            Brick b=new Brick(0,player.getX() + offsetX, player.getY() + offsetY);
//            BrickList.remove(b);

            attackCooldown =player.getAttackInterval();

        }


    }

    public synchronized void fire(Tank t) {
        Log.d("fire", "enemy fire!");
        int direction = t.getDirection();
        int offsetX = 0;
        int offsetY = 0;
        switch (direction) {
            case 0:
                offsetY = -playAreaBlockInPixel;
                break;
            case 1:
                offsetX = playAreaBlockInPixel;
                break;
            case 2:
                offsetY = playAreaBlockInPixel;
                break;
            case 3:
                offsetX = -playAreaBlockInPixel;
                break;
            default:
                break;
        }
        Projectile p = new Projectile(
                102, t.getX() + offsetX, t.getY() + offsetY, t.getDirection(), playAreaBlockInPixel);
        projectileList.add(p);
    }

    // initialize game
    public void init() {
        player = new Tank(2, screenWidth/2, screenHeight/2, 0, PLAYER1, playAreaBlockInPixel);
        attackCooldown = player.getAttackInterval();
        tankList.add(player);
//        for(int i=1; i<=5;i++){
//        brick=new Brick(0,(screenWidth/2)+playAreaBlockInPixel*2*i,(screenHeight/2)+playAreaBlockInPixel*2*i);
//        BrickList.add(brick);}




        //紅磚塊位置

        int [] brickArrayX={300,400,500,600,700,800,900,200,200,200,
                200,200,200,200,200,200,200,200,200,200,
                200,200,200,200,200,200,
                900,900,900,900,900,900,900,900,900,
                300,400,500,600,700,800,
                700,700,700,700,800,900,
                200,200,200,200,300,300,300


        };
        int [] brickArrayY={700,700,700,700,700,700,700,800,900,1000,
                1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,
                2100,2200,2300,2400,2500,2600,
                800,900,1000,1100,1300,1400,1500,1600,1700,
                1200,1200,1200,1200,1200,1200,
                100,200,300,400,400,400,
                700,400,500,600,400,500,600



        };
        int j;
        for(j=0; j< Arrays.stream(brickArrayX).count(); j++)
        {
            brick =new Brick(0,brickArrayX[j],brickArrayY[j]);
            BrickList.add(brick);
        }

        //灰磚塊位置
        int [] solidArrayX={300,300,300,300,400,500,600,600,700,800,900,700,800,900,900,
                400,500,600,800,900

        };
        int [] solidArrayY={1300,1400,1500,1500,1500,1500,1500,1300,500,500,500,600,600,600,1200,
                200,200,200,200,200

        };
        int k;
        for(k=0; k< Arrays.stream(solidArrayX).count(); k++)
        {
            brick =new Brick(0,solidArrayX[k],solidArrayY[k]);
            SolidList.add(brick);
        }

        int[] flowerArrayX={600,800,800,900,900,400,500,600};
        int[] flowerArrayY={1400,100,300,100,300,100,100,100};
        int k1;
        for(k1=0; k1< Arrays.stream(flowerArrayX).count(); k1++)
        {
            brick =new Brick(0,flowerArrayX[k1],flowerArrayY[k1]);
            flowerList.add(brick);
        }


        enemyRespawnX = screenWidth / 2;
        enemyRespawnY = screenHeight / 5;
        // for test
        Tank enemy = new Tank(0, enemyRespawnX, enemyRespawnY, 2, ENEMY, playAreaBlockInPixel);
        enemyAI = new EnemyAI(enemy, player, this, difficulty);
        tankList.add(enemy);
        enemyRespawnTime = 120;
        difficulty = 1;
    }

    public void pause(){
        playing = false;
        loopThread.setRunning(false);
        Log.d("pause","pause");
    }

    public void notifyMainActivity(String action) {
        Intent i = new Intent(action);
        mContext.sendBroadcast(i);
    }


    public void resume() {
        playing = true;
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
        notifyMainActivity(RESUMED);
    }

    public void restart() {

        playing = true;
        finished = false;
        init();
        loopThread = new LoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
    }

    // get screen width & height here
    public void surfaceCreated(SurfaceHolder holder) {
        Rect surfaceFrame = holder.getSurfaceFrame();
        screenWidth = surfaceFrame.width();
        screenHeight = surfaceFrame.height();
        Log.d("W and H", screenWidth + ", " + screenHeight);
        pixelPerBlock = screenWidth / screenRatio;
        Log.d("pixelPerBlock:", ""+pixelPerBlock);
        int sideSpace = (screenRatio - 12) / 2; //sideSpace in blocks
        playAreaLeft = sideSpace * pixelPerBlock; // we have a 4:3 game area
        playAreaRight = screenWidth - sideSpace*pixelPerBlock;
        playAreaTop = 0;
        playAreaBottom = screenHeight;

        leftUIRect = new Rect(0, 0, pixelPerBlock*sideSpace, screenHeight);
        rightUIRect = new Rect(screenWidth - pixelPerBlock*sideSpace, 0, screenWidth, screenHeight);
        playAreaBlockInPixel = (playAreaRight - playAreaLeft) / playAreaBlockWidth;
        Log.d("playAreaBlockInPixel", "" + playAreaBlockInPixel);
        projctileSize = playAreaBlockInPixel / 4;
        tankSize = playAreaBlockInPixel;
        restart();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int screenWidth,
                               int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        playing = false;
        loopThread.setRunning(false);
        while (retry) {
            try {
                loopThread.sleep(100);
                loopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}