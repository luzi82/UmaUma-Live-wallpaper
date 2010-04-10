package com.luzi82.umawallpaper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressWarnings("unchecked")
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

	static final int[] FRAME = { R.drawable.img00052, R.drawable.img00053,
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
	static SoftReference<Bitmap>[] bitmapRef = new SoftReference[FRAME_LENGTH];
	static SoftReference<byte[]>[] bufferRef = new SoftReference[FRAME_LENGTH];
	static int frame = 0;

	static int oldWidth = -1;
	static int oldHeight = -1;

	static final BitmapFactory.Options bOptions = new Options();

	static {
		bOptions.inScaled = false;
		for (int i = 0; i < FRAME_LENGTH; ++i) {
			bitmapRef[i] = new SoftReference<Bitmap>(null);
			bufferRef[i] = new SoftReference<byte[]>(null);
		}
	}

	static Timer timer;

	class LiveWallpaperEngine extends Engine {

		@Override
		public void onCreate(SurfaceHolder holder) {
			super.onCreate(holder);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		// Become false when switching to an app or put phone to sleep
		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			Log.d(LOG_TAG, "onVisibilityChanged=" + visible);
			if (visible) {
				// updateCanvas();
				if (timer == null) {
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							updateCanvas();
						}
					}, 100, 100);
				}
			} else {
				if (timer != null) {
					timer.cancel();
					timer = null;
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
			Log.d(LOG_TAG, "xOffset=" + xOffset + " yOffset=" + yOffset
					+ "xOffseStep=" + xOffsetStep + " yOffsetStep="
					+ yOffsetStep + "xPixelOffset=" + xPixelOffset
					+ " yPixelOffset=" + yPixelOffset);
			LiveWallpaper.xOffset = xOffset;
			LiveWallpaper.yOffset = yOffset;
			// updateCanvas();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			Log.d(LOG_TAG, "onSurfaceChanged");
			// updateCanvas();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			Log.d(LOG_TAG, "onSurfaceCreated");
			// updateCanvas();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.d(LOG_TAG, "onSurfaceDestroyed");
		}

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

		private void updateCanvas() {
			SurfaceHolder holder = getSurfaceHolder();
			if (holder != null) {
				Canvas c = null;
				try {
					c = holder.lockCanvas();
					if (c != null) {
						int nowWidth = c.getWidth();
						int nowHeight = c.getHeight();
						if ((nowWidth != oldWidth) || (nowHeight != oldHeight)) {
							for (int i = 0; i < FRAME_LENGTH; ++i) {
								bitmapRef[i].clear();
								bufferRef[i].clear();
							}
							oldWidth = nowWidth;
							oldHeight = nowHeight;
						}
						Bitmap currentBitmap = bitmapRef[frame].get();
						if (currentBitmap == null) {
							Log.d(LOG_TAG, "no bmp");
							byte[] currentBuffer = bufferRef[frame].get();
							if (currentBuffer == null) {
								Log.d(LOG_TAG, "no buf");
								Bitmap bmp = BitmapFactory.decodeResource(
										getResources(), FRAME[frame], bOptions);
								if (bmp != null) {
//									int bw = bmp.getWidth();
//									int bh = bmp.getHeight();
//									if ((nowHeight != bmp.getHeight())
//											|| (nowWidth != bmp.getWidth())) {
//										int nwbh = nowWidth * bh;
//										int nhbw = nowHeight * bw;
//										if (nwbh > nhbw) {
//											bh = bh * nowWidth / bw;
//											bw = nowWidth;
//										} else if (nwbh < nhbw) {
//											bw = bw * nowHeight / bh;
//											bh = nowHeight;
//										} else {
//											bw = nowWidth;
//											bh = nowHeight;
//										}
//										Log.d(LOG_TAG, "create bmp from res");
//										currentBitmap = Bitmap
//												.createScaledBitmap(bmp, bw,
//														bh, true);
//										bmp.recycle();
//										bmp = null;
									
									currentBitmap=bmp;

										ByteArrayOutputStream baos = new ByteArrayOutputStream();
										currentBitmap.compress(
												CompressFormat.PNG, 0, baos);
										try {
											baos.close();
										} catch (IOException ioe) {
											throw new Error(ioe);
										}
										Log.d(LOG_TAG, "create buf from bmp");
										currentBuffer = baos.toByteArray();
										Log.d(LOG_TAG, "store buf");
										bufferRef[frame] = new SoftReference<byte[]>(
												currentBuffer);
//									}
								}
							} else {
								Log.d(LOG_TAG, "create bmp from buf");
								currentBitmap = BitmapFactory.decodeByteArray(
										currentBuffer, 0, currentBuffer.length,
										bOptions);
							}
							Log.d(LOG_TAG, "store bmp");
							bitmapRef[frame] = new SoftReference<Bitmap>(
									currentBitmap);
						}
						if (currentBitmap != null) {
							// Paint p = new Paint();
							float x = c.getWidth() - currentBitmap.getWidth();
							// int h = c.getHeight();
							// x-=;
							x = x * xOffset;
							// c.drawRGB(0, 0, 0);
							// c.drawBitmap(test, new Rect(0,0,480,600), new
							// Rect(0,0,480,600), p);
							c.drawBitmap(currentBitmap, x, 0, null);
							// p.setColor(Color.BLACK);
							// p.setTextSize(20);
							// c.drawText("" + w + "," + h, w / 2, h / 2, p);
						}
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

}
