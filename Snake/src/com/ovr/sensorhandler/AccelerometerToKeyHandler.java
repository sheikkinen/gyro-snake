/**
 * 
 * AccelerometerToKeyHandler - Read Orientation Sensor and convert changes into key presses  
 * 
 * Copyright 2011-2012 Oulu Voice Response Ky
 *
 * @author Sami Heikkinen
 */

package com.ovr.sensorhandler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class AccelerometerToKeyHandler extends SensorToKeyHandler {	
	
	public AccelerometerToKeyHandler( Context context, KeyHandler controller, SensorConfiguration configuration ) {
		super(context, controller, configuration);
		zAverage = new MovingAverage(0);
		yAverage = new MovingAverage(0);
	}
	
	public void initialize() {
		super.initialize();
		activeSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {		
		float x = event.values[0];        
		float y = event.values[1];
        float z = event.values[2];
    
        handleOrientationChange( x, -y, z );
	}
}
