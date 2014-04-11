package com.ovr.sensorhandler;

import java.util.ArrayList;
import java.util.List;

public class MovingAverage {
	private List<Float> valueList;
	private float movingAverage=0;
	private int mListSize;
	
	public MovingAverage( int listSize ) {
		valueList = new ArrayList<Float>();
		mListSize = listSize;
	}
	
	public void add( float value ) {
		if( mListSize <= 0 ) return;		
		
		valueList.add(value);
		if (valueList.size() > mListSize){
			valueList.remove(0);
		}		
		float zTotal = 0;
		float zCount = 0;
		for (float f : valueList){
			// Assume Degrees -180 - 180 & process border values by adjusting towards current value
			if( value < -90 && f > 90 ) { 
				zTotal = zTotal + f - 360;
			} else if( value > 90 && f < -90 ) {
				zTotal = zTotal + f + 360;
			} else {
				zTotal = zTotal + f;
			}
			
			// zTotal = zTotal + f;			
			zCount++;
		}
		movingAverage = zTotal / zCount;
	}

	/**
	 * @return the movingAverage
	 */
	public float getValue() {
		return movingAverage;
	}
}
