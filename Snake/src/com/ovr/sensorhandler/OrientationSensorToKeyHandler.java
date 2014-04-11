/**
 * 
 * OrientationSensorToKeyHandler - Read Orientation Sensor and convert changes into key presses  
 * 
 * Copyright 2011-2012 Oulu Voice Response Ky
 *
 * @author Sami Heikkinen
 */

package com.ovr.sensorhandler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

public class OrientationSensorToKeyHandler extends SensorToKeyHandler {	
	
	public OrientationSensorToKeyHandler( Context context, KeyHandler controller, SensorConfiguration configuration ) {
		super(context, controller, configuration);
		zAverage = new MovingAverage(0);
		yAverage = new MovingAverage(0);
	}
	
	@SuppressWarnings("deprecation")
	public void initialize() {
		super.initialize();
		activeSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {			
		float x = event.values[0];        
		float y = event.values[1];
        float z = event.values[2];
    
        Log.d( "com.ovr.sensorhandler", "onSensorChanged: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2] );
        handleOrientationChange( x, y, -z );
	}
}
