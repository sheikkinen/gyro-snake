/**
 * 
 * OrientationSensorToKeyHandler - Read RotationVector Sensor and convert changes into key presses  
 * 
 * Copyright (C) 2012 - 2013 Oulu Voice Response Ky 
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
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class RotationVectorToKeyHandler extends SensorToKeyHandler {
	
    private final float[] rotationMatrix = new float[16];
	
	public RotationVectorToKeyHandler( Context context, KeyHandler controller, SensorConfiguration configuration ) {
		super(context,controller, configuration);
		
		// Test / Debug - remove moving average
		// zAverage = new MovingAverage(0);
		// yAverage = new MovingAverage(0);
	}
	
	public void initialize() {
		super.initialize();
		activeSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        
        // initialize the rotation matrix to identity
        rotationMatrix[ 0] = 1;
        rotationMatrix[ 4] = 1;
        rotationMatrix[ 8] = 1;
        rotationMatrix[12] = 1;
	}
	
    public void onSensorChanged(SensorEvent event) {
		if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ) {
			float[] values = new float[16];
			
			SensorManager.getRotationMatrixFromVector( rotationMatrix, event.values);
			values = SensorManager.getOrientation( rotationMatrix, values );
			
        	float x = (float)(values[0] * 180.0 / Math.PI); // azimuth / bearing 
            float y = (float)(values[1] * 180.0 / Math.PI); // pitch / nose direction           
            float z = (float)(values[2] * 180.0 / Math.PI); // roll / face tilt 
			handleOrientationChange(x, y, z);
			
			Log.d( "com.ovr.sensorhandler", "onSensorChanged: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + " / " + values[0] + ", " + values[1] + ", " + values[2] );
        }
    } 
}
