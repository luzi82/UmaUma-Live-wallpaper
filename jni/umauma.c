#include <jni.h>
#include <stdlib.h>
#include "libpng/png.h"
#include "version.h"

//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *aJvm, void *aReserved) {
//}

JNIEXPORT void JNICALL Java_com_luzi82_umawallpaper_LibUmaumaSo_getVersion(JNIEnv* env,
		jclass cls, jbyteArray out) {
	jbyte*buf=((*env)->GetByteArrayElements(env,out,NULL));
	if(buf){
		memcpy(buf,__AUTO_VERSION__,sizeof(__AUTO_VERSION__));
		(*env)->ReleaseByteArrayElements(env,out,buf,0);
	}
}
