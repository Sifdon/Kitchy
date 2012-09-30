package edu.berkeley.kitchy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import android.R.integer;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class BoardView extends SurfaceView implements Runnable, OnTouchListener {

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
			resume();
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
			//board.addCut(x1, y1, x2, y2);
			board.addCut(generateRandomCut());
			pause();
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
	}
	
	private void scaleImage(Bitmap bitmap, Canvas canvas)
	{
		Rect dst = new Rect(0, 0, canvas.getWidth(), (int) (canvas.getHeight() * scaleFactor));
		Log.d("tag", "" + scaleFactor);
		canvas.drawBitmap(bitmap, null, dst, null);
	}
	
	public int[] generateRandomCut()
	{
		int x1 = (int) (100 * scaleFactor) + board.getCuts().size() * 10 + (int)(Math.random() * 10 - 5);
		int y1 = (int) (100 * scaleFactor);
		int x2 = x1 + (int)(Math.random() * 10 - 5);
		int y2 = (int) (600 * scaleFactor);
		int[] result = {x1, y1, x2, y2};
		return result;
	}

}
