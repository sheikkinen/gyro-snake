/**
 * 
 * SensorConfiguration - Key configurations for games  
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


import android.content.res.Configuration;
import android.view.KeyEvent;

public class SensorConfiguration {
	
	private OrientationToKeyConfiguration yPositive=null;
	private OrientationToKeyConfiguration yNegative=null;
	private OrientationToKeyConfiguration zPositive=null;
	private OrientationToKeyConfiguration zNegative=null;
	private OrientationToKeyConfiguration xPositive=null;
	private OrientationToKeyConfiguration xNegative=null;

	
	public SensorConfiguration(int device) {
		switch (device){
		case Configuration.ORIENTATION_PORTRAIT:
			xPositive = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_RIGHT );
			xNegative = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_LEFT );
			zPositive = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_UP );
			zNegative = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_DOWN );
			yPositive = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_LEFT );
			yNegative = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_RIGHT );
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			xPositive = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_RIGHT );
			xNegative = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_LEFT );
			zPositive = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_LEFT );
			zNegative = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_RIGHT );
			yPositive = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_UP );
			yNegative = new OrientationToKeyConfiguration( KeyEvent.KEYCODE_DPAD_DOWN );
			break;
		}
	}
	
	public OrientationToKeyConfiguration getYPositive() {
		return yPositive;
	}

	public OrientationToKeyConfiguration getYNegative() {
		return yNegative;
	}

	public OrientationToKeyConfiguration getZPositive() {
		return zPositive;
	}

	public OrientationToKeyConfiguration getZNegative() {
		return zNegative;
	}

	public OrientationToKeyConfiguration getXPositive() {
		return xPositive;
	}

	public OrientationToKeyConfiguration getXNegative() {
		return xNegative;
	}
	
	public class OrientationToKeyConfiguration {
		private int 		keycode = 0;
		private int			currentDelay = 0;
		private int			singleTreshold = 5; // 5
		private int			preEventTreshold = 3; // 3
		private int 		delayTreshold = 100; // 5
    	

    	public OrientationToKeyConfiguration( int keycode, int singleTreshold, int preEventTreshold ) {
    		this.keycode = keycode;
    		this.preEventTreshold = preEventTreshold;
    		this.singleTreshold = singleTreshold;    		
    	}
    	
		
    	public OrientationToKeyConfiguration( int keycode ) {
    		this.keycode = keycode;
    	}
    	
		public int getKeycode() {
			return keycode;
		}

		public int getCurrentDelay() {
			return currentDelay;
		}
		
		public void setCurrentDelay(int delay) {
			currentDelay = delay;
		}

		public int getSingleTreshold() {
			return singleTreshold;
		}

		public int getPreEventTreshold() {
			return preEventTreshold;
		}

		public int getDelayTreshold() {
			return delayTreshold;
		}
    }
}
