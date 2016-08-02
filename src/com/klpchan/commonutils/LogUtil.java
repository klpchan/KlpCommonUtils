package com.klpchan.commonutils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import com.klpchan.commonutils.CommonUtilsEnv;


/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)
 */
public class LogUtil {

    public static String customTagPrefix = "custom_tag";

    private boolean DEBUG = false;
//    private static String TAG = customTagPrefix;
    private static LogUtil sInstance;

    public static boolean TOAST_EXTRA_MSG = false;

    public static LogUtil getInstance() {
        if (sInstance == null) {
            sInstance = new LogUtil();
        }
        return sInstance;
    }

    private LogUtil() {
        if (Build.TYPE.equals("eng")){
            DEBUG = true;
        } else {
            File file = new File(Environment.getExternalStorageDirectory() + "/log_enable_switcher");
            DEBUG = file.exists();
        }
    }

    public static void d(String tag, String content) {
        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.d(tag, content);
        }
    }

    public static void i(String tag, String content) {
        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void e(String tag, String content) {
        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.e(tag, content);
        }
    }

    public static void v(String tag, String content) {
        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.v(tag, content);
        }
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.d(tag, content);
        }
    }

    public static void d(String content, Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();
        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.d(tag, content, tr);
        }
    }

    public static void e(String content) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();
        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.e(tag, content);
        }
    }

    public static void e(String content, Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.e(tag, content, tr);
        }
    }

    public static void i(String content) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.i(tag, content);
        }
    }

    public static void i(String content, Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.i(tag, content, tr);
        }
    }

    public static void v(String content) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.v(tag, content);
        }
    }

    public static void v(String content, Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.v(tag, content, tr);
        }
    }

    public static void w(String content) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.w(tag, content);
        }
    }

    public static void w(String content, Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.w(tag, content, tr);
        }
    }

    public static void w(Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.w(tag, tr);
        }
    }


    public static void wtf(String content) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.wtf(tag, content);
        }
    }

    public static void wtf(String content, Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.wtf(tag, content, tr);
        }
    }

    public static void wtf(Throwable tr) {
        if (!CommonUtilsEnv.debug) return;
        String tag = generateTag();

        LogUtil logUtil = getInstance();
        if (logUtil != null && logUtil.DEBUG) {
            Log.wtf(tag, tr);
        }
    }

}
