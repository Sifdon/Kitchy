package edu.berkeley.kitchy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class BoardView extends SurfaceView implements Runnable, OnTouchListener, SensorEventListener {
	
	TextView textView;
	StringBuilder builder = new StringBuilder();
	DataStorage xAxis = new DataStorage();//stores acceleration, velocity, and position data along the x-axis
	DataStorage yAxis = new DataStorage();//stores acceleration, velocity, and position data along the y-axis
	DataStorage zAxis = new DataStorage();//stores acceleration, velocity, and position data along the z-axis
	
	private CutDeterminer c = new CutDeterminer();
	

	private Thread renderThread = null;
	private SurfaceHolder holder;
	private volatile boolean running = false;

	private Board board = new Board();
	private float scaleFactor;

	//	Bitmap bob565;
	//	Bitmap bob4444;
	Bitmap carrotBoard;
	Rect dst = new Rect();

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
		holder = getHolder();
		
		//Loads the boardBack image
		try {
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open("carrotBoard.png");
			carrotBoard = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (IOException e) {
			// silently ignored, bad coder monkey, baaad!
		} finally {
			// we should really close our input streams here.
		}

		//Draws the board when nothing has happened
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Canvas canvas = holder.lockCanvas();
				scaleFactor = canvas.getWidth() / ((float) carrotBoard.getWidth());
				drawBoard(canvas);
				holder.unlockCanvasAndPost(canvas);
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
		
		resume();

	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(!AccelerometerTest.isAccelerationGravity(event.values[0], event.values[1], event.values[2]))
		{
			double timeSeconds = event.timestamp / (Math.pow(10.0, 9));
			if(c.isSlice(event.values[0]))
				board.addCut(generateRandomCut());
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
			builder.append(AccelerometerTest.magnitudeAcceleration(event.values[0], event.values[1], event.values[2]));
			builder.append(", x-p: ");//appends position values
			builder.append(xAxis.currentPosition());
			builder.append(", y-p: ");
			builder.append(yAxis.currentPosition());
			builder.append(", z-p: ");
			builder.append(zAxis.currentPosition());
			builder.append(", #Cuts:");
			builder.append(c.numCuts());
//			Log.d("accel", builder.toString());
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

	public void resume() {
		running = true;
		renderThread = new Thread(this);
		renderThread.start();
	}

	public void run() {
		while (running) {
//			try {
				Log.d("running", "running");
				if (!holder.getSurface().isValid())
					continue;

				Canvas canvas = holder.lockCanvas();
				drawBoard(canvas);
				holder.unlockCanvasAndPost(canvas);
				//numCuts.setText("Number of cuts: " + board != null ? board.getCuts().size() : 0);
//			}
//			catch (Exception e) {
//				Log.d("?", "EXCEPTED" + e.toString());
//				//throw new NullPointerException();
//			}
		}
	}

	public void pause() {
		running = false;
		boolean retry = true;
		while (retry) {
			try {
				Log.d("trying", "running");
				renderThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// retry
			} catch (NullPointerException ne) {
				break;
			}
		}
	}

	private int x1, y1, x2, y2, maxX, maxY, offX, offY;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			resume();
			x1 = (int) event.getX();
			y1 = (int) event.getY();
			x2 = x1;
			y2 = y1;
			break;
		case MotionEvent.ACTION_MOVE:
			x2 = (int) event.getX();
			y2 = (int) event.getY();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			board.addCut(x1, y1, x2, y2);
			//board.addCut(generateRandomCut());
			//pause();
			break;
		}
		return true;
	}

	private void drawBoard(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#7a4622"));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setDither(true);
		
		

		canvas.drawRGB(255, 255, 255);
		scaleImage(carrotBoard, canvas);
		Vector<int[]> cuts = (Vector<int[]>) board.getCuts().clone();
		for (int[] cut : cuts) {
			canvas.drawLine(cut[0], cut[1], cut[2], cut[3], paint);
		}
		
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setStrokeWidth(1);
		paint.setTextSize(30);
		canvas.drawText("Number of cuts: " + board.getCuts().size(), 100, 400, paint);
	}
	
	private void scaleImage(Bitmap bitmap, Canvas canvas)
	{
		Rect dst = new Rect(0, 0, canvas.getWidth(), (int) (canvas.getHeight() * scaleFactor));
		Log.d("tag", "" + scaleFactor);
		canvas.drawBitmap(bitmap, null, dst, null);
	}
	
	public int[] generateRandomCut()
	{
		if (board.getCuts().size() >= 50)
			board.resetCuts();
		
		int x1 = (int) (120 * scaleFactor) + board.getCuts().size() * 10 + (int)(Math.random() * 10 - 5);
		int y1 = (int) (100 * scaleFactor);
		int x2 = x1 + (int)(Math.random() * 10 - 5);
		int y2 = (int) (350 * scaleFactor);
		Log.d("tag", "" + scaleFactor);
		int[] result = {x1, y1, x2, y2};
		return result;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
