package com.klpchan.commonutils;

import android.app.Application;

public class CommonUtilsEnv {
    public static Application app;
    
    public static boolean debug;
    
    public static void init(Application application) {
        app = application;
    }
    
    public static Application app() {
        return app;
    }
    
    public static void setDebug(boolean debugmode) {
        debug = debugmode;
    }
    
    public static void setCustomePerfix(String prefix) {
        LogUtil.customTagPrefix = prefix;
    }
   
    /**
     * The switcher for whether shortcut title is Chinese.
     * it used for only-chinese project, it create the shortcut title in chinese with any locale. 
     */
    public static boolean SWITCHER_SHORTCUT_CHINESE_ALWASY = false;
    /**
     * The switcher for update verion for shortcut
     * True: it use packageName instead of appName to judge whether current shortcut exist or not.
     * False: it use appName
     */
    public static final boolean SWITCHER_SHORTCUT_UPDATE= true;
}
