#include <jni.h>
#include <stdlib.h>
#include "libpng/png.h"
#include "version.h"
#include <android/log.h>
#include <setjmp.h>

//JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *aJvm, void *aReserved) {
//}

//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "libumauma",__VA_ARGS__)
#define LOGD(...)

typedef struct {
	png_bytep buf;
	int size;
	int offset;
} special_read_struct;

void special_read(png_structp png_ptr, png_bytep data, png_size_t length);
void decode(png_bytep out, int outSize, png_bytep src, int srcSize, int x,
		int y, int w, int h);

JNIEXPORT void JNICALL Java_com_luzi82_umawallpaper_LibUmaumaSo_getVersion(JNIEnv* env,
		jclass cls, jbyteArray out) {
	jbyte*buf=((*env)->GetByteArrayElements(env,out,NULL));
	if(buf) {
		memcpy(buf,__AUTO_VERSION__,sizeof(__AUTO_VERSION__));
		(*env)->ReleaseByteArrayElements(env,out,buf,0);
	}
}

JNIEXPORT void JNICALL Java_com_luzi82_umawallpaper_LibUmaumaSo_decode(JNIEnv* env,
		jclass cls, jbyteArray out, jbyteArray src,jint x,jint y,jint w,jint h) {
	static jbyte*outBuf,*srcBuf;
	static jsize len;

	outBuf=(*env)->GetByteArrayElements(env,out,NULL);
	if(outBuf) {
		srcBuf=(*env)->GetByteArrayElements(env,src,NULL);
		if(srcBuf) {
			decode(outBuf,(*env)->GetArrayLength(env,out),srcBuf,(*env)->GetArrayLength(env,src),x,y,w,h);
			(*env)->ReleaseByteArrayElements(env,src,srcBuf,0);
		}
		(*env)->ReleaseByteArrayElements(env,out,outBuf,0);
	}
}

void decode(png_bytep out, int outSize, png_bytep src, int srcSize,
		png_int_32 xx, png_int_32 yy, png_int_32 ww, png_int_32 hh) {
	static png_structp png_ptr;
	static png_infop info_ptr;
	static png_uint_32 width, height;
	static int bit_depth, color_type, interlace_type;
	static png_uint_32 row;
	static int number_passes, pass;
	static int x, y;

	special_read_struct special_read_ins;

	png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
	if (png_ptr == NULL) {
		LOGD("!!! png_ptr == NULL");
		return;
	}

	info_ptr = png_create_info_struct(png_ptr);
	if (info_ptr == NULL) {
		LOGD("!!! info_ptr == NULL");
		png_destroy_read_struct(&png_ptr, NULL, NULL);
		return;
	}

	if (setjmp(png_jmpbuf(png_ptr))) {
		/* Free all of the memory associated with the png_ptr and info_ptr */
		png_destroy_read_struct(&png_ptr, &info_ptr, NULL);
		LOGD("!!! setjmp(png_jmpbuf(png_ptr))");
		/* If we get here, we had a problem reading the file */
		return;
	}

	LOGD("after setjmp");

	special_read_ins.buf = src;
	special_read_ins.size = srcSize;
	special_read_ins.offset = 0;

	LOGD("png_set_read_fn");
	png_set_read_fn(png_ptr, (png_voidp)(&special_read_ins), special_read);

	LOGD("png_set_sig_bytes");
	png_set_sig_bytes(png_ptr, 0);

	LOGD("png_read_info");
	png_read_info(png_ptr, info_ptr);

	LOGD("png_get_IHDR");
	png_get_IHDR(png_ptr, info_ptr, &width, &height, &bit_depth, &color_type,
			&interlace_type, NULL, NULL);

	LOGD("color_type %08x",color_type);

	//	png_set_tRNS_to_alpha(png_ptr);

	//	if (color_type & PNG_COLOR_MASK_COLOR)
	//	png_set_bgr(png_ptr);
	png_set_add_alpha(png_ptr, 0xff, PNG_FILLER_AFTER);
	number_passes = png_set_interlace_handling(png_ptr);
	png_read_update_info(png_ptr, info_ptr);

	//	png_set_swap_alpha(png_ptr);

	//	png_read_end(png_ptr, info_ptr);
	//
	//	png_destroy_read_struct(&png_ptr, &info_ptr, NULL);

	LOGD("number_passes %d",number_passes);

	//	number_passes = png_set_interlace_handling(png_ptr);
	//
	//	for (pass = 0; pass < number_passes; pass++) {
	//		for (y = 0; y < height; y++) {
	//			png_read_rows(png_ptr, &row_pointers[y], NULL, 1);
	//		}
	//	}

	int rowbytes = png_get_rowbytes(png_ptr, info_ptr);
	//	png_bytep row_pointer = png_malloc(png_ptr, rowbytes);

	//	png_bytep outPtr=out;
	//	png_int_32 ww2=ww<<2;
	//	for (row = 0; row < 480; row++) {
	//		png_read_row(png_ptr,row_pointer,NULL);
	//		memcpy(outPtr,row_pointer,ww2);
	//		outPtr+=ww2;
	//	}

	//	png_bytep outPtr=out;
	//	int* in0=(int*)row_pointer;
	//	int* inE=in0+400;
	//	int* in2;
	//	int* out1=(int*)outPtr;
	//	int* out2,*out3,*out4,*out5;
	//	int skip=ww<<1;
	//	int j;
	//	//	png_int_32 hh2=hh<<2;
	//	for (row = 0; row < 120; ++row) {
	//		png_read_row(png_ptr,row_pointer,NULL);
	////		in2=in0;
	////		memcpy(outPtr,row_pointer,hh2);
	//		out2=out1;
	//		out3=out2+1;
	//		out4=out2+ww;
	//		out5=out4+1;
	//		for(in2=in0;in2!=inE;++in2){
	//			*out2=*in2;
	//			*out3=*in2;
	//			*out4=*in2;
	//			*out5=*in2;
	//			out2+=skip;
	//			out3+=skip;
	//			out4+=skip;
	//			out5+=skip;
	//		}
	//		out1+=2;
	//	}
	//
	//	png_free(png_ptr, row_pointer);

	png_bytep row_pointer = png_malloc(png_ptr, rowbytes * height);
	png_bytep row_pointerV[1024];
	png_bytep p = row_pointer;
	for (y = 0; y != height; ++y) {
		row_pointerV[y] = p;
		p += rowbytes;
	}
	png_read_image(png_ptr, row_pointerV);

	int*in0, *in1;
	in0 = ((int*) row_pointer) + (xx >> 1);
	int*out0, *out1;
	out0 = (int*) out;
	out1 = out0 + ww;
	const int hhh = hh >> 1;
	int www = ww >> 1;
	if (xx & 1) {
		--www;
		for (y = 0; y != hhh; ++y) {
			in1 = in0;
			*(out0++) = *in1;
			*(out1++) = *in1;
			++in1;
			for (x = 0; x != www; ++x) {
				*(out0++) = *in1;
				*(out0++) = *in1;
				*(out1++) = *in1;
				*(out1++) = *in1;
				++in1;
			}
			*(out0++) = *in1;
			*(out1++) = *in1;
			++in1;
			in0 += width;
			out0 = out1;
			out1 += ww;
		}
	} else {
		for (y = 0; y != hhh; ++y) {
			in1 = in0;
			for (x = 0; x != www; ++x) {
				*(out0++) = *in1;
				*(out0++) = *in1;
				*(out1++) = *in1;
				*(out1++) = *in1;
				++in1;
			}
			in0 += width;
			out0 = out1;
			out1 += ww;
		}
	}

	png_free(png_ptr, row_pointer);

	//	png_bytep outPtr=out;
	//	for (row = 0; row < height; row++) {
	////		row_pointers[row] = NULL;
	////		row_pointers[row] = png_malloc(png_ptr, rowbytes);
	//		row_pointers[row]=outPtr;
	//		outPtr+=rowbytes;
	//	}

	//	png_read_image(png_ptr, row_pointers);

	//	png_free(png_ptr, row_pointer);
	//
	//	png_read_end(png_ptr, info_ptr);

	//	for (row = 0; row < height; row++) {
	//		memcpy(outPtr,row_pointers[row],rowbytes);
	//		outPtr+=rowbytes;
	//		png_free(png_ptr, row_pointers[row]);
	//		row_pointers[row] = NULL;
	//	}

	png_destroy_read_struct(&png_ptr, &info_ptr, NULL);

	LOGD("decode good end");
}

void special_read(png_structp png_ptr, png_bytep data, png_size_t length) {
	png_voidp special_read_ins_p0 = png_get_io_ptr(png_ptr);
	special_read_struct*special_read_ins_p =
			(special_read_struct*) special_read_ins_p0;
	memcpy(data, special_read_ins_p->buf + special_read_ins_p->offset, length);
	special_read_ins_p->offset += length;
	if (special_read_ins_p->offset > special_read_ins_p->size) {
		LOGD("!!! libumauma.so: offset>size");
	}
}
