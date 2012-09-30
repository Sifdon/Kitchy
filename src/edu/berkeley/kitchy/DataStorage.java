package edu.berkeley.kitchy;

import java.util.ArrayList;
/**
 * This class is used to store data values for the acceleration,
 * velocity, and position values for an object.
 * It only represents the values in one-dimension though.
 * @author Eric
 *
 */
public class DataStorage 
{
	//represents the acceleration in a two-element array-list, first element is acceleration, second is time
//	private ArrayList<ArrayList<Double>> acceleration = new ArrayList<ArrayList<Double>>();
	private ArrayList<double []> acceleration = new ArrayList<double[]>();
	
	//represents the velocity in a two-element array, first element is velocity, second is time
	private ArrayList<double []> velocity = new ArrayList<double[]>();
	
	//represents the position in a two-element array, first element is position, second is time
	private ArrayList<double []> position = new ArrayList<double[]>();
	
	/**
	 * Adds a data value to this storage system and changes the acceleration, velocity, and position
	 * accordingly
	 * @param acceleration - acceleration value
	 * @param time - time of measurement
	 */
	public void addDataValue(double acceleration, double time)
	{
		addDataValue(acceleration, time, false);
	}

	/**
	 * Adds a data value to this storage system. If this acceleration is due to more than just gravity,
	 * it will only record the acceleration value.
	 * @param acceleration
	 * @param time
	 * @param isGravity - whether this data value is due to just gravity
	 * true - if just gravity, false if more than just gravity
	 */
	public void addDataValue(double acceleration, double time, boolean isGravity) 
	{
		if (isGravity == true) 
		{
			addAcceleration(acceleration, time);
			
		} 
		else 
		{
			addAcceleration(acceleration, time);// adds the acceleration value into storage
			nextVelocityValue();// calculates and adds the next velocity value
			nextPositionValue();// calculates and adds the next position value
		}
	}
	
	/**
	 * Adds an acceleration to the acceleration array list
	 * @param acceleration - acceleration value
	 * @param time - time at which measurement is taken
	 */
	public void addAcceleration(double acceleration, double time)
	{
		double[] value = {acceleration, time};
		this.acceleration.add(value);
	}
	
	/**
	 * Returns the acceleration array list
	 * @return acceleration array list
	 */
	public ArrayList<double []> getAcceleration()
	{
		return acceleration;
	}
	
	/**
	 * Creates the next velocity value by taking (a1 + a2)*dt/2, then increments
	 * it to the previous velocity value. Also creates the next time value
	 * by taking t1 - t2.
	 * It then adds both values in the form {next velocity, time} to the velocity array list
	 */
	public void nextVelocityValue()
	{
		if(acceleration.size() < 2)//ends if there aren't two data points to average
		{
			return;
		}
		double[] lastIndex = acceleration.get(acceleration.size() - 1);//most recent value for acceleration
		double[] secondLastIndex = acceleration.get(acceleration.size() - 2);//second most recent value for acceleration
		double averageAcceleration = (lastIndex[0] + secondLastIndex[0])/2;
		double dt = lastIndex[1] - secondLastIndex[1];
		double averageTime = (lastIndex[1] + secondLastIndex[1])/2;
		double[] velocityValue = {0,0};
		if(velocity.size() == 0)//if this is the first data value
		{
//			velocityValue = {averageAcceleration*dt, averageTime};
			velocityValue[0] = averageAcceleration*dt;//assumes initial value is 0
			velocityValue[1] = averageTime;
		}
		else
		{
			double previousVelocity = velocity.get(velocity.size() - 1)[0];
//			velocityValue = {previousVelocity + averageAcceleration*dt, averageTime};
			velocityValue[0] = previousVelocity + averageAcceleration*dt;
			velocityValue[1] = averageTime;
		}
		velocity.add(velocityValue);
	}
	
	/**
	 * Returns the velocity array list
	 * @return - velocity array list
	 */
	public ArrayList<double []> getVelocity()
	{
		return velocity;
	}
	
	/**
	 * 
	 * Creates the next position value by taking (v1 + v2)*dt/2, then increments
	 * it to the previous position value. Also creates the next time value
	 * by taking t1 - t2.
	 * It then adds both values in the form {next position, time} to the position array list
	 */
	public void nextPositionValue()
	{
		if(velocity.size() < 2)//ends if there aren't two data points to average
		{
			return;
		}
		double[] lastIndex = velocity.get(velocity.size() - 1);//most recent data value for velocity
		double[] secondLastIndex = velocity.get(velocity.size() - 2);//second most recent velocity value
		double averageVelocity = (lastIndex[0] + secondLastIndex[0])/2;
		double dt = lastIndex[1] - secondLastIndex[1];
		double averageTime = (lastIndex[1] + secondLastIndex[1])/2;
		double[] positionValue = {0, 0};
		
		if(position.size() == 0)//if this is the first value in the list
		{
//			velocityValue = {averageAcceleration*dt, averageTime};
			positionValue[0] = averageVelocity*dt;//assumes initial position is 0
			positionValue[1] = averageTime;
		}//otherwise
		else
		{
			double previousPosition = position.get(position.size() - 1)[0];
//			velocityValue = {previousVelocity + averageAcceleration*dt, averageTime};
			positionValue[0] = previousPosition + averageVelocity*dt;
			positionValue[1] = averageTime;
		}
		position.add(positionValue);
	}
	
	/**
	 * Returns the current position in regards to the initial position
	 * @return - a position as a double, or 0 if there are no position values yet
	 */
	public double currentPosition()
	{
		if(position.size() == 0)
		{
			return 0;
		}
		return position.get(position.size() - 1)[0];
	}
	
	/**
	 * Returns the current velocity in regards to the initial position
	 * @return - a velocity as a double, or 0 if there is no velocity values yet
	 */
	public double currentVelocity()
	{
		if(velocity.size() == 0)
		{
			return 0;
		}
		return velocity.get(velocity.size() - 1)[0];
	}
	
	/**
	 * Returns the most recently recorded value for acceleration
	 * @return - an acceleration value as a double, or 0 if there are no acceleration values yet
	 */
	public double currentAcceleration()
	{
		if(acceleration.size() == 0)
		{
			return 0;
		}
		return acceleration.get(acceleration.size() - 1)[0];
	}
	
	/**
	 * Returns the position array list
	 * @return - position array list
	 */
	public ArrayList<double []> getPosition()
	{
		return position;
	}
	
	/**
	 * Prints an array list with two element doubles as elements
	 * (Note: Second element is assumed to be time)
	 * @param s - the name of the first value
	 * @param ArrayList<double[]>
	 */
	public static void testPrint(String s, ArrayList<double[]> list)
	{
		for(double[] value: list)
		{
			System.out.println(s + " = " + value[0] + ", t = " + value[1]);
		}
	}
	
	/**
	 * Tester method
	 * @param args
	 */
	public static void main(String args[])
	{
		DataStorage data = new DataStorage();
//		d.addAcceleration(5, 5);
//		d.addAcceleration(4, 6);
//		d.addAcceleration(3, 7);
//		d.addAcceleration(2, 8);
		for(double d = 0; d < 2.01; d += .01)
		{
			data.addDataValue(d, d);
		}
//		data.addDataValue(5, 5);
//		data.addDataValue(4, 6);
//		data.addDataValue(3, 7);
//		data.addDataValue(2, 8);
		System.out.println("accelerations");
		testPrint("a", data.getAcceleration());
		System.out.println("velocities");
		testPrint("v", data.getVelocity());
		System.out.println("positions");
		testPrint("p", data.getPosition());
	}
}