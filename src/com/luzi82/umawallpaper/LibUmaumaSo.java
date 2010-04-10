package com.luzi82.umawallpaper;

public class LibUmaumaSo {

	public static native void getVersion(byte[] out);

	public static native void decode(byte[] out, byte[] src, int x, int y,
			int w, int h);

	static {
		System.loadLibrary("umauma");
	}

}
