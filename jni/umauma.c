#include <jni.h>
#include <stdlib.h>
#include "libpng/png.h"

jint sImgCount = -1;
jbyte**sPngBuf = NULL;

jint sOriWidth = 0;
jint sOriHeight = 0;
jint sOriPixSize = 0;
jint sOriBufSize = 0;

jint sClipWidth = 0;
jint sClipHeight = 0;
jint sClipPixSize = 0;
jint sClipBufSize = 0;

jbyte*sOriBmpBuf = NULL;
jbyte*sClipBmpBuf = NULL;
jint sCurClipBufSize = 0;

void clean() {
	free(sOriBmpBuf);
	sOriBmpBuf = NULL;
	free(sClipBmpBuf);
	sClipBmpBuf = NULL;
	sCurClipBufSize = 0;
}

void checkBuf() {
	if (!sOriBmpBuf) {
		sOriBmpBuf = malloc(sClipBufSize);
	}
	if (sCurClipBufSize != sClipBufSize) {
		free(sClipBmpBuf);
		sClipBmpBuf = NULL;
		sClipBmpBuf = malloc(sClipBufSize);
		sCurClipBufSize = sClipBufSize;
	}
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *aJvm, void *aReserved) {
	clean();
	free(sPngBuf);
	sBuffer=NULL;
}

JNIEXPORT void JNICALL Java_com_luzi82_umawallpaper_LiveWallpaper_init(
		JNIEnv* aEnv,jclass aCls,
		jint aImgCount,
		jint aOriWidth,jint aOriHeight
) {
	if(sImgCount!=-1)return;
	sPngBuf=malloc(imgCount*sizeof(jbyte*));
	sOriWidth=aOriWidth;
	sOriHeight=aOriHeight;
	sOriPixSize=sOriWidth*sOriHeight;
	sOriBufSize=sOriPixSize*sizeof(jint);
}

JNIEXPORT void JNICALL Java_com_luzi82_umawallpaper_LiveWallpaper_setClipSize(
		JNIEnv* aEnv,jclass aCls,
		jint aClipWidth,jint aClipHeight
) {
	sClipWidth = aClipWidth;
	sClipHeight = aClipHeight;
	sClipPixSize = sClipWidth*sClipHeight;
	sClipBufSize = sClipPixSize*sizeof(jint);
}

JNIEXPORT void JNICALL Java_com_luzi82_umawallpaper_LiveWallpaper_setPngBuf(
		JNIEnv* aEnv,jclass aCls,
		jint aIndex,jByteArray aBuf,jint aSize
) {
	if(!sPngBuf)return;
	if(aIndex<0)return;
	if(aIndex>=sImgCount)return;
	free(sPngBuf[aIndex]);
	sPngBuf[aIndex]=NULL;
	sPngBuf[aIndex]=
}

//JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_cleanBuf(JNIEnv* env,
//		jclass cls) {
//	clean();
//}
//
//JNIEXPORT void JNICALL Java_com_luzi82_randomwallpaper_LiveWallpaper_genRandom(JNIEnv* env,
//		jclass cls, jbyteArray out) {
//	static jint*ptr;
//	if(buf) {
//		ptr=buf;
//		while(ptr!=bufEnd) {
//			seed=(seed*multiplier+0xbLL)&((1LL<<48)-1);
//			*ptr=(int)((seed>>(48-32))|0xff000000);
//			ptr++;
//		}
//		(*env)->SetByteArrayRegion(env,out,0,byteSize,(jbyte*)buf);
//	}
//}


