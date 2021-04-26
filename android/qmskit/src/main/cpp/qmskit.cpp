#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_nevmem_qms_qmskit_QMSKit_apiKey(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "API_KEY";
    return env->NewStringUTF(hello.c_str());
}
