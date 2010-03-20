LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

libpng_SRC_FILES := \
	libpng/png.c \
	libpng/pngerror.c \
	libpng/pngget.c \
	libpng/pngmem.c \
	libpng/pngpread.c \
	libpng/pngread.c \
	libpng/pngrio.c \
	libpng/pngrtran.c \
	libpng/pngrutil.c \
	libpng/pngset.c \
	libpng/pngtrans.c \
	libpng/pngwio.c \
	libpng/pngwrite.c \
	libpng/pngwtran.c \
	libpng/pngwutil.c

umauma_SRC_FILES := umauma.c

LOCAL_LDLIBS := -lz
LOCAL_MODULE := umauma
LOCAL_SRC_FILES := $(libpng_SRC_FILES) $(umauma_SRC_FILES)

include $(BUILD_SHARED_LIBRARY)
