package edu.berkeley.kitchy;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AccelerometerTest extends Activity implements SensorEventListener {
	TextView textView;
	StringBuilder builder = new StringBuilder();
	DataStorage xAxis = new DataStorage();//stores acceleration, velocity, and position data along the x-axis
	DataStorage yAxis = new DataStorage();//stores acceleration, velocity, and position data along the y-axis
	DataStorage zAxis = new DataStorage();//stores acceleration, velocity, and position data along the z-axis
	
	private CutDeterminer c = new CutDeterminer();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		textView = new TextView(this);
		setContentView(textView);

		SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
			textView.setText("No accelerometer installed");
		} else {
			Sensor accelerometer = manager.getSensorList(
					Sensor.TYPE_ACCELEROMETER).get(0);
			if (!manager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_GAME)) {
				textView.setText("Couldn't register sensor listener");
			}
		}
	}
	
	/**
	 * Determines whether an acceleration is due to just gravity by finding the magnitude of the
	 * acceleration in three dimensions and determining whether it is close enough to 9.8
	 * @param xAcceleration
	 * @param yAcceleration
	 * @param zAcceleration
	 * @return true if it appears to be just gravity, false if there is some sort of user interaction
	 */
	public static boolean isAccelerationGravity(double xAcceleration, double yAcceleration, double zAcceleration)
	{
		double magnitude = Math.sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration + zAcceleration*zAcceleration);
		double delta = .5;
		if(Math.abs(magnitude - 9.8) < delta)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Returns the magnitude of an acceleration in three dimensions
	 * @param xAcceleration
	 * @param yAcceleration
	 * @param zAcceleration
	 * @return the overall magnitude
	 */
	public static double magnitudeAcceleration(double xAcceleration, double yAcceleration, double zAcceleration)
	{
		return Math.sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration + zAcceleration*zAcceleration);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(!isAccelerationGravity(event.values[0], event.values[1], event.values[2]))
		{
			double timeSeconds = event.timestamp / (Math.pow(10.0, 9));
			c.isSlice(event.values[0]);
			xAxis.addDataValue(event.values[0], timeSeconds);//adds new data values
			yAxis.addDataValue(event.values[1], timeSeconds);
			zAxis.addDataValue(event.values[2], timeSeconds);
			builder.setLength(0);
			builder.append("x-a: ");//appends acceleration values
			builder.append(event.values[0]);
			builder.append(", y-a: ");
			builder.append(event.values[1]);
			builder.append(", z-a: ");
			builder.append(event.values[2]);
			builder.append(", mag-a: ");
			builder.append(magnitudeAcceleration(event.values[0], event.values[1], event.values[2]));
			builder.append(", x-p: ");//appends position values
			builder.append(xAxis.currentPosition());
			builder.append(", y-p: ");
			builder.append(yAxis.currentPosition());
			builder.append(", z-p: ");
			builder.append(zAxis.currentPosition());
			builder.append(", #Cuts:");
			builder.append(c.numCuts());
			textView.setText(builder.toString());
			Log.d("accel", builder.toString());
			c.resetConsecutiveGravityAccelerations();
		}
		else
		{
			c.incrementConsecutiveGravityAccelerations();
			if(c.numConsecutiveGravityAccelerations() > 4)//if phone has faced same direction long enough
			{
				if(event.values[0] > 0)
				{
					c.setDirection(true);//sets direction of the phone if gravity is positive
				}
				else
				{
					c.setDirection(false);//sets direction of the phone to down if gravity is negative
				}
				c.resetConsecutiveGravityAccelerations();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing to do here
	}
}