package edu.berkeley.kitchy;

/**
 * This class has methods to determine whether something is a cut.
 * @author Eric
 *
 */
public class CutDeterminer 
{
	public static final int UP = 1;//constants that represent which directions acceleration is in
	public static final int DOWN = 2;
	public static final int JUST_GRAVITY = 0;
	
	private int cuts = 0;//number of cuts
	private int consecutiveNonGravityAccelerations = 0;//number of consecutive accelerations not attributable to gravity
	private int currentDirection = 0;//the current direction which the phone is moving
	private int numSameDirection = 0;//counts the number of accelerations that are in the same direction that are not attributable to gravity
	
	private boolean direction;//true means gravity is positive, false means gravity is negative
	private int consecutiveGravityAccelerations = 0;//counts the number of accelerations due to gravity
	
	/**
	 * 
	 * @return
	 */
	public boolean isSlice(double acceleration)
	{
		if(currentDirection == isJustGravity(acceleration))
		{
			numSameDirection++;
		}
		else
		{
			numSameDirection = 0;
		}
		
		if(consecutiveNonGravityAccelerations > 2 && currentDirection == isJustGravity(acceleration) && numSameDirection < 5)
		{
			cuts++;
			consecutiveNonGravityAccelerations = 0;
			currentDirection = isJustGravity(acceleration);
			return true;
		}
		
		if(isJustGravity(acceleration) == JUST_GRAVITY)//acceleration is just due to gravity
		{	
			consecutiveNonGravityAccelerations = 0;//resets consecutive nongravitational accelerations
			currentDirection = JUST_GRAVITY;//changes direction
		}
		else if(isJustGravity(acceleration) == UP)//extra acceleration is up
		{
			if(currentDirection == UP)//same direction
			{
				consecutiveNonGravityAccelerations++;//increments
			}
			else if(currentDirection == JUST_GRAVITY || currentDirection == DOWN)
			{
				consecutiveNonGravityAccelerations = 0;//resets consecutive nongravitational accelerations is different direction
			}
			currentDirection = UP;//changes direction to down
		}
		else//extra acceleration is down
		{
			if(currentDirection == DOWN)
			{
				consecutiveNonGravityAccelerations++;//adds one if same direction
			}
			else if(currentDirection == JUST_GRAVITY || currentDirection == UP)
			{
				consecutiveNonGravityAccelerations = 0;
			}
			currentDirection = DOWN;//changes direction to down
		}
		return false;
	}
	
	/**
	 * Determines whether an acceleration is due just to gravity
	 * or whether it could be due to human interaction
	 * @return true if just gravity, false if more than gravity
	 */
	public int isJustGravity(double acceleration)
	{
		if (Math.abs(trueGravity() - acceleration) < 4)//if the acceleration is just due to gravity
		{
			return JUST_GRAVITY;
		}
		else if(trueGravity() - acceleration > 0)//if the phone is going up
		{
			return UP;
		}
		else//if the phone is moving down
		{
			return DOWN;
		}
	}
	
	/**
	 * Sets the direction that the phone is pointing downwards
	 * true means gravity is positive, false means gravity is negative
	 * @param direction - the direction
	 */
	public void setDirection(boolean direction)
	{
		this.direction = direction;
	}
	
	/**
	 * Returns the number of consecutive accelerations due to gravity
	 * Used for determining the direction that the phone is facing
	 * @return - the number of consecutive gravity accelerations
	 */
	public int numConsecutiveGravityAccelerations()
	{
		return consecutiveGravityAccelerations;
	}
	
	/**
	 * Increments the consecutive gravity accelerations by 1
	 */
	public void incrementConsecutiveGravityAccelerations()
	{
		consecutiveGravityAccelerations++;
	}
	
	/**
	 * Sets the consecutive gravity accelerations to 0
	 */
	public void resetConsecutiveGravityAccelerations()
	{
		consecutiveGravityAccelerations = 0;
	}
	
	/**
	 * Determines what the true acceleration value of gravity is, based on the direction
	 * that the phone is facing
	 * @return - +9.8 if positive x-axis is in direction of gravity,
	 * -9.8 is negative y-axis is in direction of gravity
	 */
	public double trueGravity()
	{
		if(direction == true)
		{
			return 9.8;
		}
		else
		{
			return -9.8;
		}
	}
	
	/**
	 * Returns the number of cuts
	 * @return
	 */
	public int numCuts()
	{
		return cuts;
	}
}
