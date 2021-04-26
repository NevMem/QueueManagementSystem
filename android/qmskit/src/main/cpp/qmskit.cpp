#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_nevmem_qms_qmskit_QMSKit_apiKey(
        JNIEnv* env,
        jobject /* this */) {

#define APP_API_KEY(X) \
    static std::string API_KEY = X;
#include "../../../../../shared/apikey/apikey.def"
#undef APP_API_KEY

    return env->NewStringUTF(API_KEY.c_str());
}
