package com.luzi82.umawallpaper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class LiveWallpaper extends WallpaperService {

	public static final String LOG_TAG = "LiveWallpaper";

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		// Create the live wallpaper engine
		return new LiveWallpaperEngine();
	}

	// static Matrix m0 = new Matrix();
	// static Bitmap test = null;
	static float xOffset = 0;
	static float yOffset = 0;

	static final int[] FRAME = { R.raw.img00052_0, R.drawable.img00053,
			R.drawable.img00054, R.drawable.img00055, R.drawable.img00056,
			R.drawable.img00057, R.drawable.img00058, R.drawable.img00059,
			R.drawable.img00060, R.drawable.img00061, R.drawable.img00062,
			R.drawable.img00063, R.drawable.img00064, R.drawable.img00065,
			R.drawable.img00066, R.drawable.img00067, R.drawable.img00068,
			R.drawable.img00069, R.drawable.img00070, R.drawable.img00071,
			R.drawable.img00072, R.drawable.img00073, R.drawable.img00074,
			R.drawable.img00075, R.drawable.img00076, R.drawable.img00077,
			R.drawable.img00078, R.drawable.img00079, };
	static int FRAME_LENGTH = FRAME.length;
	byte[][] pngBuf = new byte[FRAME_LENGTH][];
	static int frame = 0;

	static int oldWidth = -1;
	static int oldHeight = -1;

	static final Matrix mi = new Matrix();
	static final Paint paint = new Paint();
	static {
		paint.setColor(Color.rgb(0x00, 0xff, 0x00));
	}

	static byte[] byteAry;
	static ByteBuffer byteBuffer;
	static Bitmap bitmap;

	static final BitmapFactory.Options bOptions = new Options();

	static {
		bOptions.inScaled = false;
	}

	static void cleanBitmap() {
		// Log.d(LOG_TAG, "static synchronized void clean()");
		synchronized (mi) {
			// size = -1;
			oldWidth = -1;
			oldHeight = -1;
			byteAry = null;
			byteBuffer = null;
			if (bitmap != null)
				bitmap.recycle();
			bitmap = null;
			// cleanBuf();
		}
	}

	class LiveWallpaperEngine extends Engine {

		Timer timer;

		// TODO should put to static in final
		byte[] testPngBuf;

		void cleanPng() {
			synchronized (mi) {
				testPngBuf = null;
			}
		}

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
			Log.d(LOG_TAG, "public void onCreate(SurfaceHolder holder)");
			synchronized (mi) {
				try {
					AssetFileDescriptor afd = getResources().openRawResourceFd(
							FRAME[0]);
					testPngBuf = new byte[(int) (afd.getLength())];
					InputStream is = afd.createInputStream();
					int v = is.read(testPngBuf);
					is.close();
					Log.d(LOG_TAG, String.format("read %d=%d",
							testPngBuf.length, v));
				} catch (IOException e) {
					e.printStackTrace();
					testPngBuf = null;
				}
			}
		}

		@Override
		public void onDestroy() {
			synchronized (mi) {
				cleanEngine();
			}
			super.onDestroy();
		}

		// Become false when switching to an app or put phone to sleep
		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			synchronized (mi) {
				// Log.d(LOG_TAG, "onVisibilityChanged=" + visible);
				if (visible) {
					startEngine();
				} else {
					cleanEngine();
				}
			}

		}

		// // 0 when on the first home screen, -0.5/-160px on the center
		// // home screen (assume 3 screens in total).
		// @Override
		// public void onOffsetsChanged(float xOffset, float yOffset,
		// float xOffsetStep, float yOffsetStep, int xPixelOffset,
		// int yPixelOffset) {
		// super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
		// xPixelOffset, yPixelOffset);
		// Log.d(LOG_TAG, "xOffset=" + xOffset + " yOffset=" + yOffset
		// + "xOffseStep=" + xOffsetStep + " yOffsetStep="
		// + yOffsetStep + "xPixelOffset=" + xPixelOffset
		// + " yPixelOffset=" + yPixelOffset);
		// LiveWallpaper.xOffset = xOffset;
		// LiveWallpaper.yOffset = yOffset;
		// // updateCanvas();
		// }

		// @Override
		// public void onSurfaceChanged(SurfaceHolder holder, int format,
		// int width, int height) {
		// super.onSurfaceChanged(holder, format, width, height);
		// Log.d(LOG_TAG, "onSurfaceChanged");
		// // updateCanvas();
		// }
		//
		// @Override
		// public void onSurfaceCreated(SurfaceHolder holder) {
		// super.onSurfaceCreated(holder);
		// Log.d(LOG_TAG, "onSurfaceCreated");
		// // updateCanvas();
		// }
		//
		// @Override
		// public void onSurfaceDestroyed(SurfaceHolder holder) {
		// super.onSurfaceDestroyed(holder);
		// Log.d(LOG_TAG, "onSurfaceDestroyed");
		// }

		// @Override
		// public void onTouchEvent(MotionEvent event) {
		// super.onTouchEvent(event);
		// // Change the wallpaper color
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// Log.d(LOG_TAG, "touch!");
		// Random rand = new Random(System.currentTimeMillis());
		// int r = rand.nextInt(256);
		// int g = rand.nextInt(256);
		// int b = rand.nextInt(256);
		// updateWallpaperColor(r, g, b);
		// }
		// }

		// private void updateWallpaperColor(int r, int g, int b) {
		// // Get the SurfaceHolder
		// SurfaceHolder holder = getSurfaceHolder();
		// Canvas c = null;
		// try {
		// c = holder.lockCanvas();
		// if (c != null) {
		// c.drawRGB(r, g, b);
		// }
		// } finally {
		// if (c != null)
		// holder.unlockCanvasAndPost(c);
		// }
		// }

		private long last = 0;

		private void updateCanvas() {
			synchronized (mi) {
				if (!isVisible())
					return;
				if (testPngBuf == null)
					return;
				SurfaceHolder holder = getSurfaceHolder();
				if (holder != null) {
					Canvas c = null;
					try {
						c = holder.lockCanvas();
						if (c != null) {
//							 int nowWidth = 1067;
//							 int nowHeight = 800;
							int nowWidth = c.getWidth();
							int nowHeight = c.getHeight();
							int s = (nowWidth * nowHeight) << 2;
							if ((nowWidth != oldWidth)
									|| (nowHeight != oldHeight)) {
								Log.d(LOG_TAG, "new bitmap");
								cleanBitmap();
								bitmap = Bitmap.createBitmap(nowWidth,
										nowHeight, Bitmap.Config.ARGB_8888);
								oldHeight = nowHeight;
								oldWidth = nowWidth;
							}
							if (byteAry == null || byteAry.length != s) {
								Log.d(LOG_TAG, "new buffer");
								byteAry = new byte[s];
								byteBuffer = ByteBuffer.wrap(byteAry);
								// setSize(s);
								// size = s;
							}
							// c.drawColor(Color.BLACK);
							// Bitmap
							// bmp=BitmapFactory.decodeResource(getResources(),
							// R.drawable.img00053,bOptions);
							// c.drawBitmap(bmp, mi, paint);
							// bmp.recycle();
							// bmp=null;
							LibUmaumaSo.decode(byteAry, testPngBuf, 0, 0,
									nowWidth, nowHeight);
							bitmap.copyPixelsFromBuffer(byteBuffer);
							c.drawBitmap(bitmap, mi, paint);
							long now = System.currentTimeMillis();
							if (last > 0) {
								int diff = (int) (now - last);
								c.drawText("" + diff, 100, 75, paint);
							}
							last = now;
						}
					} finally {
						if (c != null)
							holder.unlockCanvasAndPost(c);
					}
				}
				++frame;
				frame %= FRAME_LENGTH;
			}
		}

		void createTimer() {
			// Log
			// .d(LOG_TAG,
			// "static void createTimer(final LiveWallpaperEngine engine) start");
			synchronized (mi) {
				if (timer == null) {
					// Log.d(LOG_TAG, "create timer");
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							if (System.currentTimeMillis()
									- scheduledExecutionTime() > 5)
								return;
							updateCanvas();
						}
					}, 1, 1);
				}
			}
			// Log
			// .d(LOG_TAG,
			// "static void createTimer(final LiveWallpaperEngine engine) end");
		}

		void clearTimer() {
			synchronized (mi) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
			}
		}

		public void startEngine() {
			createTimer();
		}

		public void cleanEngine() {
			clearTimer();
			cleanBitmap();
			// TODO should run in final
			// cleanPng();
		}

	}

}
