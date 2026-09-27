#include <jni.h>
#include <unistd.h>
#include <string.h>
#include <android/log.h>

#include "byedpi/error.h"
#include "byedpi/proxy.h"
#include "byedpi/params.h"
#include "utils.h"

JNIEXPORT jint JNI_OnLoad(
        __attribute__((unused)) JavaVM *vm,
        __attribute__((unused)) void *reserved) {
    default_params = params;
    return JNI_VERSION_1_6;
}

JNIEXPORT jint JNICALL
Java_com_fofajardo_antidpeye_core_ByeDpiProxy_jniCreateSocketWithCommandLine(
        JNIEnv *env,
        __attribute__((unused)) jobject thiz,
        jobjectArray args) {
    return create_socket_from_cmdline(env, args);
}

JNIEXPORT jint JNICALL
Java_com_fofajardo_antidpeye_core_ByeDpiProxy_jniCreateSocket(
        JNIEnv *env,
        __attribute__((unused)) jobject thiz,
        jstring ip,
        jint port,
        jint max_connections,
        jint buffer_size,
        jint default_ttl,
        jboolean custom_ttl,
        jboolean no_domain,
        jboolean desync_http,
        jboolean desync_https,
        jboolean desync_udp,
        jint desync_method,
        jint split_position,
        jboolean split_at_host,
        jint fake_ttl,
        jstring fake_sni,
        jbyte custom_oob_char,
        jboolean host_mixed_case,
        jboolean domain_mixed_case,
        jboolean host_remove_spaces,
        jboolean tls_record_split,
        jint tls_record_split_position,
        jboolean tls_record_split_at_sni,
        jint hosts_mode,
        jstring hosts,
        jboolean tfo,
        jint udp_fake_count,
        jboolean drop_sack,
        jint fake_offset,
        jboolean no_ipv6,
        jstring conn_ip,
        jboolean wait_send,
        jint await_int,
        jboolean md5sig,
        jstring fake_data,
        jstring fake_tls_mod,
        jint tlsminor,
        jstring round,
        jstring pf,
        jstring ipset,
        jstring auto_val,
        jstring auto_mode,
        jstring timeout) {
    return create_socket_from_ui(
            env,
            ip,
            port,
            max_connections,
            buffer_size,
            default_ttl,
            custom_ttl,
            no_domain,
            desync_http,
            desync_https,
            desync_udp,
            desync_method,
            split_position,
            split_at_host,
            fake_ttl,
            fake_sni,
            custom_oob_char,
            host_mixed_case,
            domain_mixed_case,
            host_remove_spaces,
            tls_record_split,
            tls_record_split_position,
            tls_record_split_at_sni,
            hosts_mode,
            hosts,
            tfo,
            udp_fake_count,
            drop_sack,
            fake_offset,
            no_ipv6,
            conn_ip,
            wait_send,
            await_int,
            md5sig,
            fake_data,
            fake_tls_mod,
            tlsminor,
            round,
            pf,
            ipset,
            auto_val,
            auto_mode,
            timeout);
}

JNIEXPORT jint JNICALL
Java_com_fofajardo_antidpeye_core_ByeDpiProxy_jniStartProxy(
        __attribute__((unused)) JNIEnv *env,
        __attribute__((unused)) jobject thiz,
        jint fd) {
    LOG(LOG_S, "start_proxy, fd: %d", fd);
    int res = start_event_loop(fd);
    dump_all_cache();
    close(fd);
    reset_params();
    if (res < 0) {
        uniperror("start_event_loop");
        return get_e();
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_fofajardo_antidpeye_core_ByeDpiProxy_jniStopProxy(
        __attribute__((unused)) JNIEnv *env,
        __attribute__((unused)) jobject thiz,
        jint fd) {
    LOG(LOG_S, "stop_proxy, fd: %d", fd);
    return stop_proxy_loop(fd);
}