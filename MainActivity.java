package com.example.tank1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
  //  private Gam
    private FrameLayout mFrame;
    private GameViewNew gameView;
    private View ui;
    private TextView resumeHint;
    private BroadcastReceiver receiver;
    private Button leftButton;
    private Button rightButton;
    private Button upButton;
    private Button downButton;
    private Button aButton;
    private Button bButton;
    private Button pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        gameView = new GameViewNew(this);
        mFrame = new FrameLayout(this);
        LayoutInflater inflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ui=inflater.inflate(R.layout.ui,null);
        pauseButton = (Button) ui.findViewById(R.id.pause);
        resumeHint = (TextView) ui.findViewById(R.id.textView1);

//        button.setOnClickListener(btnshow);

//        private View.OnClickListener btnshow=new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        }
//

        leftButton = (Button) ui.findViewById(R.id.left);
        leftButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.LEFT, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.LEFT, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });

        rightButton = (Button) ui.findViewById(R.id.right);
        rightButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.RIGHT, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.RIGHT, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });


        upButton = (Button) ui.findViewById(R.id.up);
        upButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.UP, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.UP, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });

        downButton = (Button) ui.findViewById(R.id.down);
        downButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.DOWN, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.DOWN, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });

        aButton = (Button) ui.findViewById(R.id.a_button);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gameView.fire();
            }
        });

        bButton = (Button) ui.findViewById(R.id.b_button);
        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeGame() ;
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseGame();
            }
        });



        // handle controls
        IntentFilter filter = new IntentFilter();
        filter.addAction(GameViewNew.RESUMED);
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == GameViewNew.RESUMED) {
                    resumeGame();
                }
            }
        };
        registerReceiver(receiver, filter);

        mFrame.addView(gameView);
        mFrame.addView(ui);

        setContentView(mFrame);






    }

    public  void reStarGame()
    {
        mFrame.removeView(gameView);
        gameView = new GameViewNew(this);
        mFrame = new FrameLayout(this);
        LayoutInflater inflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ui=inflater.inflate(R.layout.ui,null);
        pauseButton = (Button) ui.findViewById(R.id.pause);
        resumeHint = (TextView) ui.findViewById(R.id.textView1);

//        button.setOnClickListener(btnshow);

//        private View.OnClickListener btnshow=new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        }
//

        leftButton = (Button) ui.findViewById(R.id.left);
        leftButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.LEFT, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.LEFT, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });

        rightButton = (Button) ui.findViewById(R.id.right);
        rightButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.RIGHT, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.RIGHT, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });


        upButton = (Button) ui.findViewById(R.id.up);
        upButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.UP, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.UP, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });

        downButton = (Button) ui.findViewById(R.id.down);
        downButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.movePlayer(GameViewNew.DOWN, GameViewNew.STOP);
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
                    gameView.movePlayer(GameViewNew.DOWN, GameViewNew.MOVING);
                    return true;
                }
                return false;
            }

        });

        aButton = (Button) ui.findViewById(R.id.a_button);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gameView.fire();
            }
        });

        bButton = (Button) ui.findViewById(R.id.b_button);
        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseGame();
            }
        });



        // handle controls
        IntentFilter filter = new IntentFilter();
        filter.addAction(GameViewNew.RESUMED);
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == GameViewNew.RESUMED) {
                    resumeGame();
                }
            }
        };

        mFrame.addView(gameView);
        mFrame.addView(ui);

        setContentView(mFrame);
//        Intent intent =getIntent();
//        finish();
//        startActivity(intent);
    }

    public void pauseGame() {
        gameView.pause();
        resumeHint.setVisibility(View.VISIBLE);
        ui.setBackgroundColor(ContextCompat.getColor(
                MainActivity.this,R.color.lightGray));
        pauseButton.setVisibility(View.INVISIBLE);
    }

    public void resumeGame() {
        gameView.resume();
        resumeHint.setVisibility(View.INVISIBLE);
        ui.setBackgroundColor(Color.TRANSPARENT);
        pauseButton.setVisibility(View.VISIBLE);
    }

    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }



    }
