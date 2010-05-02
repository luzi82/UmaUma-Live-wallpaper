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
		cleanBitmap();
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		// Create the live wallpaper engine
		return new LiveWallpaperEngine();
	}

	// static Matrix m0 = new Matrix();
	// static Bitmap test = null;
	// static float yOffset = 0;

	static final int[] FRAME_P = { R.raw.img00052_p, R.raw.img00053_p,
			R.raw.img00054_p, R.raw.img00055_p, R.raw.img00056_p,
			R.raw.img00060_p, R.raw.img00061_p, R.raw.img00064_p,
			R.raw.img00068_p, R.raw.img00069_p, R.raw.img00070_p,
			R.raw.img00071_p, R.raw.img00073_p, R.raw.img00074_p, };
	static final int[] FRAME_L = { R.raw.img00052_l, R.raw.img00053_l,
			R.raw.img00054_l, R.raw.img00055_l, R.raw.img00056_l,
			R.raw.img00060_l, R.raw.img00061_l, R.raw.img00064_l,
			R.raw.img00068_l, R.raw.img00069_l, R.raw.img00070_l,
			R.raw.img00071_l, R.raw.img00073_l, R.raw.img00074_l, };
	static int FRAME_LENGTH = FRAME_P.length;

	int WIDTH_P = 1066;
	
	int REFRESH_PERIOD=80;

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
		byte[][] pngBuf = new byte[FRAME_LENGTH][];
		// int frame = 0;
		int[] activeFrame = null;
		float xOffset = 0;
		boolean loadDone=false;

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
			Log.d(LOG_TAG, "public void onCreate(SurfaceHolder holder)");
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

		// 0 when on the first home screen, -0.5/-160px on the center
		// home screen (assume 3 screens in total).
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			// Log.d(LOG_TAG, "xOffset=" + xOffset + " yOffset=" + yOffset
			// + "xOffseStep=" + xOffsetStep + " yOffsetStep="
			// + yOffsetStep + "xPixelOffset=" + xPixelOffset
			// + " yPixelOffset=" + yPixelOffset);
			this.xOffset = xOffset;
			// LiveWallpaper.yOffset = yOffset;
			// updateCanvas();
		}

//		private long last = 0;

		private void updateCanvas() {
			synchronized (mi) {
				if (!isVisible())
					return;
				int frame = (int) ((System.currentTimeMillis() / REFRESH_PERIOD)
						% FRAME_LENGTH);
				SurfaceHolder holder = getSurfaceHolder();
				if (holder != null) {
					Canvas c = null;
					try {
						c = holder.lockCanvas();
						if (c != null) {
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
								cleanPng();
							}
							if (byteAry == null || byteAry.length != s) {
								Log.d(LOG_TAG, "new buffer");
								byteAry = new byte[s];
								byteBuffer = ByteBuffer.wrap(byteAry);
							}
							if (loadDone) {
								if (activeFrame == FRAME_L) {
									LibUmaumaSo.decode(byteAry, pngBuf[frame],
											0, 0, nowWidth, nowHeight);
								} else if (activeFrame == FRAME_P) {
									int x = (WIDTH_P - nowWidth) >> 1;
									if (!isPreview()) {
										x = (int) ((WIDTH_P - nowWidth) * xOffset);
										if (x < 0)
											x = 0;
										if (x > WIDTH_P - nowWidth)
											x = WIDTH_P - nowWidth;
									}
									LibUmaumaSo.decode(byteAry, pngBuf[frame],
											x, 0, nowWidth, nowHeight);
								}
								bitmap.copyPixelsFromBuffer(byteBuffer);
								c.drawBitmap(bitmap, mi, paint);

//								long now = System.currentTimeMillis();
//								if (last > 0) {
//									int diff = (int) (now - last);
//									c.drawText("" + diff, 100, 75, paint);
//								}
//								last = now;
							}
						}
					} finally {
						if (c != null)
							holder.unlockCanvasAndPost(c);
					}
				}
				if (!loadDone) {
					loadPng();
				}
				// ++frame;
				// frame %= FRAME_LENGTH;
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
									- scheduledExecutionTime() > 10)
								return;
							updateCanvas();
						}
					}, REFRESH_PERIOD, REFRESH_PERIOD);
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

		void cleanPng() {
			synchronized (mi) {
				loadDone=false;
				for (int i = 0; i < FRAME_LENGTH; ++i) {
					pngBuf[i] = null;
				}
			}
		}

		void loadPng() {
			synchronized (mi) {
				try {
					activeFrame = null;
					if (oldWidth == 480 && oldHeight == 800) {
						activeFrame = FRAME_P;
					}
					if (oldWidth == 800 && oldHeight == 480) {
						activeFrame = FRAME_L;
					}
					if (activeFrame != null) {
						for (int i = 0; i < FRAME_LENGTH; ++i) {
							AssetFileDescriptor afd = getResources()
									.openRawResourceFd(activeFrame[i]);
							pngBuf[i] = new byte[(int) (afd.getLength())];
							InputStream is = afd.createInputStream();
							is.read(pngBuf[i]);
							is.close();
						}
					}
					loadDone=true;
				} catch (IOException e) {
					e.printStackTrace();
					// testPngBuf = null;
				}
			}
		}

		public void startEngine() {
			createTimer();
		}

		public void cleanEngine() {
			clearTimer();
			cleanBitmap();
			cleanPng();
		}

	}

}
