/**
 * 
 * OrientationSensorToKeyHandler - Base class for Sensors and for converting results into key presses  
 * 
 * Copyright (C) 2011 - 2013 Oulu Voice Response Ky 
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
 
 * @author Sami Heikkinen, Mikko Heikkinen
 */

package com.ovr.sensorhandler;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;

public abstract class SensorToKeyHandler implements SensorEventListener {
	protected KeyHandler mController = null;
	protected SensorManager sm = null;		
	protected Context mContext;
	protected SensorConfiguration mConfiguration;
	protected MovingAverage zAverage;
	protected MovingAverage yAverage;
	protected MovingAverage xAverage;
	
	public SensorToKeyHandler( Context context, KeyHandler controller, SensorConfiguration configuration ) {
		Log.d( "com.ovr.sensorhandler", "SensorToKeyHandler()" );
		mController = controller;
		mContext = context;
		mConfiguration = configuration;
		
		xAverage = new MovingAverage(20);		
		zAverage = new MovingAverage(20);
		yAverage = new MovingAverage(20);
	}
	
	public void initialize() {
		Log.d( "com.ovr.sensorhandler", "initialize()" );
        sm = (SensorManager) mContext.getSystemService( Context.SENSOR_SERVICE );
	}
	
	// Updated Sensor Handling
    protected Sensor activeSensor;
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    
	public void registerListener() {
		Log.d( "com.ovr.sensorhandler", "registerListener()" );
		sm.registerListener(this, activeSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void unregisterListener() {
		Log.d( "com.ovr.sensorhandler", "unregisterListener()" );
		sm.unregisterListener(this);
	}

    protected void orientationToKeyEvent( float orientation, SensorConfiguration.OrientationToKeyConfiguration config ) {
    	orientationToDiscreteKeyEvent( orientation, config );
    	
    }
    
    private void orientationToDiscreteKeyEvent( float orientation, SensorConfiguration.OrientationToKeyConfiguration config )
    {
    	boolean isNewEventAllowed = (config.getCurrentDelay() == 0 );
    	boolean isSingleEvent = ( orientation > config.getSingleTreshold() );
    	boolean isPreEvent = (!isSingleEvent && ( orientation > config.getPreEventTreshold() ));
    	    	
    	// Generate new Key Down
    	if( isSingleEvent && isNewEventAllowed ) {
    		KeyEvent event = new KeyEvent( KeyEvent.ACTION_DOWN, config.getKeycode() );
    		mController.dispatchKeyEvent(event);
    		config.setCurrentDelay(config.getDelayTreshold());    		
    	}
    	    	
    	// Inform that Key Down will be generated if movement is bigger
    	if( isPreEvent && isNewEventAllowed ) {
    		KeyEvent event = new KeyEvent( KeyEvent.ACTION_MULTIPLE, config.getKeycode() );
    		mController.dispatchKeyEvent(event);    		    		
    	}
    	    	
    	// Clear key press after few calls - short delay
    	// For some applications key press equals to a full sequence of key down & up 
    	if( !isNewEventAllowed ) {
    		config.setCurrentDelay(config.getCurrentDelay() - 1);

    		if( !isSingleEvent ) {	
        		config.setCurrentDelay(0);	
        	}

    		if( config.getCurrentDelay() == 0 ) {
    			KeyEvent event = new KeyEvent( KeyEvent.ACTION_UP, config.getKeycode() );
	    		mController.dispatchKeyEvent(event);
    		}
    	}
    }
        
	protected void handleOrientationChange(float x, float y, float z) {

		// Handle raw values
		// mController.handleOrientationChange(x, y, z);
		
		// Compare the current value to the recent average to reduce the need to 
		// "reset" your head position and to ignore any bias in the values, for example
		// the tilt of the glasses' sensors 
		zAverage.add(z);
		z = z - zAverage.getValue();		
		
		xAverage.add(x);
		x = x - xAverage.getValue();
		
		yAverage.add(y);
		y = y - yAverage.getValue();
		
		// Print rotation vector values for debugging 
//		mController.log(x+", "+y+", "+z+"  ***");
		
		// Send only commands from one event source instead of spamming both			
		if( Math.abs( x ) > Math.abs( z ) ) { 
			z = 0.0F;
		} else {
			x = 0.0F;
		}		
		
		// Handle adjusted values
		mController.handleOrientationChange(x, y, z);		
		
		// Recon: pitch to up/down (not in use)
		orientationToKeyEvent( + y, mConfiguration.getYPositive() );
		orientationToKeyEvent( - y, mConfiguration.getYNegative() );

		// Recon: roll to left/right
		orientationToKeyEvent( + z, mConfiguration.getZPositive() );
		orientationToKeyEvent( - z, mConfiguration.getZNegative() );	

		// Recon: bearing to left/right		
		orientationToKeyEvent( + x, mConfiguration.getXPositive() );
		orientationToKeyEvent( - x, mConfiguration.getXNegative() );		
	}
}
