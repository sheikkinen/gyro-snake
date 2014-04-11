package com.ovr.sensorhandler;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

public class SensorCalibration {	
	private SensorToKeyHandler sensorToKeyHandler = null;
	private KeyHandler mHandler;
	private Activity mActivity;

	public SensorCalibration( Activity activity, KeyHandler handler ) {
		Log.d("com.ovr.sensorhandler", "SensorCalibration(Activity activity, KeyHandler handler)" );
		
		mActivity = activity;
		mHandler = handler;		
	}
	
	/**
     * Initialization of RotationVector if available. Default Accelerometer
     * 
     */
	@SuppressWarnings("deprecation")
	public void initialize() 
	{   
		Log.d("com.ovr.sensorhandler", "SensorCalibration.initialize()" );
		
		SensorManager sm = (SensorManager) mActivity.getSystemService( Context.SENSOR_SERVICE );
		
		List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
		
		if( sensorList.contains(sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)) ) {
			Log.d("com.ovr.sensorhandler", "Activating RotationVector" );
			startRotationVector(mHandler);
		} else if( sensorList.contains(sm.getDefaultSensor(Sensor.TYPE_ORIENTATION)) ) {
			Log.d("com.ovr.sensorhandler", "Activating OrientationSensor" );
			startOrientationSensor(mHandler);	
		} else if( sensorList.contains(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) ) {
			Log.d("com.ovr.sensorhandler", "Activating Accelerometer" );
			startAccelerometer(mHandler);	
		} else {
			Log.d("com.ovr.sensorhandler", "No Sensor" );
		}		
	}

	public static int getDeviceDefaultOrientation(Context context) {
	    WindowManager windowManager =  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

	    Configuration config = context.getResources().getConfiguration();

	    int rotation = windowManager.getDefaultDisplay().getRotation();	   
	    
	    if ( ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
	            config.orientation == Configuration.ORIENTATION_LANDSCAPE)
	        || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&    
	            config.orientation == Configuration.ORIENTATION_PORTRAIT))
	      return Configuration.ORIENTATION_LANDSCAPE;	    
	    
	    return Configuration.ORIENTATION_PORTRAIT;
	}
	
	private SensorConfiguration getRotationVectorConfiguration() {
		int config = getDeviceDefaultOrientation(mActivity);
		Log.d("com.ovr.sensorhandler", "RotationVector - Config:" + config );
		return new SensorConfiguration( config );
	}
	
	private SensorConfiguration getAccelerometerConfiguration() {
		// TODO: Select / define proper configs for Orientation sensors
		int config = getDeviceDefaultOrientation(mActivity);
		Log.d("com.ovr.sensorhandler", "Accelerometer - Config:" + config );
		return new SensorConfiguration( config );
	}
	
	private SensorConfiguration getOrientationSensorConfiguration() {
		// TODO: Select / define proper configs for Orientation sensors
		int config = getDeviceDefaultOrientation(mActivity);
		Log.d("com.ovr.sensorhandler", "Orientation - Config:" + config );
		return new SensorConfiguration( config );
	}
	

	private void startOrientationSensor(KeyHandler keyHandler) {
		if( sensorToKeyHandler == null ) {
			sensorToKeyHandler = 			
				new OrientationSensorToKeyHandler(				
						mActivity, // Context - needed for Sensor registrations 
						keyHandler, 
						getOrientationSensorConfiguration() );
	
	        sensorToKeyHandler.initialize();
	        sensorToKeyHandler.registerListener();
        }
	}

	
	private void startAccelerometer(KeyHandler keyHandler) {
		if( sensorToKeyHandler == null ) {
			sensorToKeyHandler = 			
				new OrientationSensorToKeyHandler(				
						mActivity, // Context - needed for Sensor registrations 
						keyHandler, 
						getAccelerometerConfiguration() );
	
	        sensorToKeyHandler.initialize();
	        sensorToKeyHandler.registerListener();
        }
	}

	public void startRotationVector(KeyHandler keyHandler)
	{	
		if( sensorToKeyHandler == null ) {
	        sensorToKeyHandler = 			
				new RotationVectorToKeyHandler(				
						mActivity, // Context - needed for Sensor registrations 
						keyHandler, 
						getRotationVectorConfiguration() );
	
	        sensorToKeyHandler.initialize();
	        sensorToKeyHandler.registerListener();
		}
	}

	public void unregisterListener() {
		if( sensorToKeyHandler != null ) {
			sensorToKeyHandler.unregisterListener();
		}
	}

	public void registerListener() {
		if( sensorToKeyHandler != null ) {
			sensorToKeyHandler.registerListener();
		}
	}
}
