/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * OVR Additions only:
 * 
 * Copyright (C) 2013 Oulu Voice Response Ky 
 *
 * @author Mikko Heikkinen
 */

package com.example.android.snake;

import com.ovr.sensorhandler.Constants;
import com.ovr.sensorhandler.KeyHandler;
import com.ovr.sensorhandler.OrientationSensorToKeyHandler;
import com.ovr.sensorhandler.RotationVectorToKeyHandler;
import com.ovr.sensorhandler.SensorCalibration;
import com.ovr.sensorhandler.SensorConfiguration;
import com.ovr.sensorhandler.SensorToKeyHandler;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Snake: a simple game that everyone can enjoy.
 * 
 * This is an implementation of the classic Game "Snake", in which you control a serpent roaming
 * around the garden looking for apples. Be careful, though, because when you catch one, not only
 * will you become longer, but you'll move faster. Running into yourself or the walls will end the
 * game.
 * 
 */
public class Snake extends Activity {

    /**
     * Constants for desired direction of moving the snake
     */
    public static int MOVE_LEFT = 0;
    public static int MOVE_UP = 1;
    public static int MOVE_DOWN = 2;
    public static int MOVE_RIGHT = 3;

    private static String ICICLE_KEY = "snake-view";

    private SnakeView mSnakeView;
	private SensorToKeyHandler sensorToKeyHandler = null;
	private SensorCalibration sensorCalibration = null;
	public final String tag = "GyroSnake";
	private TextView mTextView;
	
	private ImageView iconUp;
	private ImageView iconDown;
	private ImageView iconLeft;
	private ImageView iconRight;

	private ImageView iconUp2;
	private ImageView iconDown2;
	private ImageView iconLeft2;
	private ImageView iconRight2;
	
    /**
     * Called when Activity is first created. Turns off the title bar, sets up the content views,
     * and fires up the SnakeView.
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
             
        setContentView(R.layout.snake_layout);

        mTextView = (TextView)findViewById(R.id.text);
        mSnakeView = (SnakeView) findViewById(R.id.snake);
        mSnakeView.setDependentViews((TextView) findViewById(R.id.text),
                findViewById(R.id.arrowContainer), findViewById(R.id.background));
        
        iconUp = (ImageView)findViewById(R.id.imageUp);
        iconDown = (ImageView)findViewById(R.id.imageDown);
        iconLeft = (ImageView)findViewById(R.id.imageLeft);
        iconRight = (ImageView)findViewById(R.id.imageRight);
        iconUp2 = (ImageView)findViewById(R.id.imageUp2);
        iconDown2 = (ImageView)findViewById(R.id.imageDown2);
        iconLeft2 = (ImageView)findViewById(R.id.imageLeft2);
        iconRight2 = (ImageView)findViewById(R.id.imageRight2);
        
        log("onCreate");
        
        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
            mSnakeView.setMode(SnakeView.READY);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
            }
        }
        mSnakeView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSnakeView.getGameState() == SnakeView.RUNNING) {
                    // Normalize x,y between 0 and 1
                    float x = event.getX() / v.getWidth();
                    float y = event.getY() / v.getHeight();

                    // Direction will be [0,1,2,3] depending on quadrant
                    int direction = 0;
                    direction = (x > y) ? 1 : 0;
                    direction |= (x > 1 - y) ? 2 : 0;

                    // Direction is same as the quadrant which was clicked
                    mSnakeView.moveSnake(direction);

                } else {
                    // If the game is not running then on touching any part of the screen
                    // we start the game by sending MOVE_UP signal to SnakeView
                    mSnakeView.moveSnake(MOVE_UP);
                }
                return false;
            }
        });        
        
        initialize();
        
        // Force the screen on. Touch events won't be generated with motion steering
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mSnakeView.setMode(SnakeView.PAUSE);        
        sensorCalibration.unregisterListener();
        // sensorToKeyHandler.unregisterListener();
    }
    
    @Override
    protected void onResume(){
    	super.onResume();    	
    	
    	// Gear - initialize here
    	sensorCalibration.initialize();
    	
    	// initializeSensors();
    	sensorCalibration.registerListener();
    	//sensorToKeyHandler.registerListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store the game state
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

    /**
     * Handles key events in the game. Update the direction our snake is traveling based on the
     * DPAD.
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        	
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mSnakeView.moveSnake(MOVE_UP);
                // log("GOING UP");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mSnakeView.moveSnake(MOVE_RIGHT);
                // log("GOING RIGHT");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mSnakeView.moveSnake(MOVE_DOWN);                
                // log("GOING DOWN");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mSnakeView.moveSnake(MOVE_LEFT);
                // log("GOING LEFT");
                break;
        }

        return super.onKeyDown(keyCode, msg);
    }

    private void setDirectionIcon(int keyCode) {
    	
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                iconUp.setVisibility(View.VISIBLE);    
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                iconRight.setVisibility(View.VISIBLE);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                iconDown.setVisibility(View.VISIBLE);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                iconLeft.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setDirectionIcon2(int keyCode) {
    	
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                iconUp2.setVisibility(View.VISIBLE);    
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                iconRight2.setVisibility(View.VISIBLE);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                iconDown2.setVisibility(View.VISIBLE);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                iconLeft2.setVisibility(View.VISIBLE);
                break;
        }
    }
    
    private void clearDirectionIcon() {
    	iconUp.setVisibility(View.INVISIBLE);       
        iconDown.setVisibility(View.INVISIBLE);
        iconLeft.setVisibility(View.INVISIBLE);
        iconRight.setVisibility(View.INVISIBLE);

    	iconUp2.setVisibility(View.INVISIBLE);       
        iconDown2.setVisibility(View.INVISIBLE);
        iconLeft2.setVisibility(View.INVISIBLE);
        iconRight2.setVisibility(View.INVISIBLE);
    }
    
	public void log(String text){
		Log.d(tag, text);		
	}	

	private void initializeSensors(){		
		log( "initializeSensors()");
		sensorCalibration = new SensorCalibration(this, new KeyHandler() {
			@Override
			public void dispatchKeyEvent(KeyEvent event) {		
				log( "dispatchKeyEvent(KeyEvent event)");
				clearDirectionIcon();

				if( event.getAction() == KeyEvent.ACTION_MULTIPLE ) {
					setDirectionIcon(event.getKeyCode());							
				}
				
				if( event.getAction() == KeyEvent.ACTION_DOWN ) {
					setDirectionIcon2(event.getKeyCode());
					onKeyDown(event.getKeyCode(), event);			
				}
			}

			@Override
			public void handleOrientationChange(float x, float y, float z) {
				// Show Debug values
				// mTextView.setVisibility(View.VISIBLE);
				// mTextView.setText( (int)x + " / " + (int)y + " / " + (int)z);				
			}	
		});
	
		if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE )
		{	
			sensorCalibration.initialize();
		}
	} 
	
	private void initialize(){
		log( "initialize()");
		initializeSensors();
        // initializeSensorToKeyHandler();
        
		// Gear - no LANDSCAPE
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);		
	}
}
