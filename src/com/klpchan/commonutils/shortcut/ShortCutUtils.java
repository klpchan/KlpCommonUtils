package com.klpchan.commonutils.shortcut;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import java.util.List;

import com.klpchan.commonutils.CommonUtilsEnv;
import com.klpchan.commonutils.LogUtil;

/**
 * Created by klpchan on 2016/1/18.
 */
public class ShortCutUtils {

	@Deprecated
    public static Context mainContext; 
    /**
     * 返回添加到桌面快捷方式的Intent：
     * 1.给Intent指定action="com.android.launcher.INSTALL_SHORTCUT"
     * 2.给定义为Intent.EXTRA_SHORTCUT_INENT的Intent设置与安装时一致的action(必须要有)
     * 3.添加权限:com.android.launcher.permission.INSTALL_SHORTCUT
     */
    public static Intent getShortcutToDesktopIntent(Context context,Class<?> clazz,int appNameID, int drawabeID) {
        Intent intent = new Intent();
//        intent.setClass(context, context.getClass());
        intent.setClass(context, clazz);
        /*以下两句是为了在卸载应用的时候同时删除桌面快捷方式*/
/*        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");*/
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重建
        shortcut.putExtra("duplicate", false);
        // 设置名字
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,context.getString(appNameID));
        // 设置图标
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, drawabeID));
        // 设置意图和快捷方式关联程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);

        return shortcut;

    }

    @Deprecated
    public static void createShortCut(Class<?> clazz,int appNameID,int drawableID){
        if (mainContext == null){
            throw new IllegalArgumentException("createShortCut fail");
        } else {
            mainContext.sendBroadcast(getShortcutToDesktopIntent(mainContext,clazz,appNameID,drawableID));
        }
    }

    /**
     * 删除快捷方式
     * */
    @Deprecated
    public static void deleteShortCut(int appNameID)
    {
        if (mainContext == null){
            throw  new IllegalArgumentException("deleteShortCut fail");
        } else {
            Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
            //快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,mainContext.getString(appNameID));
            /**删除和创建需要对应才能找到快捷方式并成功删除**/
            Intent intent = new Intent();
            intent.setClass(mainContext, mainContext.getClass());
/*            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");*/

            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
            mainContext.sendBroadcast(shortcut);
        }
    }

    /**
     * Create shortcut for special target activity.
     * @param context : context of activity 
     * @param appNameID : name of shortcut
     * @param drawableID : drawable of shorcut
     * @param fixChineseTitle : fix chinese text if necessary.
     */
    public static void  addShortcut(Context context,int appNameID,int drawableID,String fixChineseTitle){
        if (context == null){
            LogUtil.e("addShortcut error : context is null");
            return;
        }
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式的名称
        if (CommonUtilsEnv.SWITCHER_SHORTCUT_CHINESE_ALWASY){
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, fixChineseTitle);
        } else {
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appNameID));
        }
        shortcut.putExtra("duplicate", false); //不允许重复创建
        //指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
        //注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
        String appClass = context.getPackageName() + ".demo.MainActivity";
        ComponentName comp = new ComponentName(context.getPackageName(),appClass);
        LogUtil.d("addShortcut,appClass is " + appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_VIEW).setComponent(comp));
        //快捷方式的图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, drawableID);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut);
    }

    /**
     * Delete shortcut for special target activity.
     * @param context : context of activity 
     * @param appNameID : name of shortcut
     * @param fixChineseTitle : fix chinese text if necessary.
     */
    public static void delShortcut(Context context,int appNameID,String fixChineseTitle){
        if (context == null){
            LogUtil.e("delShortcut error : context is null");
            return;
        }
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //快捷方式的名称
        if (CommonUtilsEnv.SWITCHER_SHORTCUT_CHINESE_ALWASY){
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,fixChineseTitle);
        } else {
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, CommonUtilsEnv.SWITCHER_SHORTCUT_UPDATE ?
                    getShortCutTitleByPackageName(context): context.getString(appNameID));
        }
        //指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
        //注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
        String appClass = context.getPackageName() +".demo.MainActivity";
        ComponentName comp = new ComponentName(context.getPackageName(), appClass);
        LogUtil.d("delShortcut,appClass is " + appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_VIEW).setComponent(comp));
        context.sendBroadcast(shortcut);
    }

    /**
     * Judge whether shortcut exist by title.
     * It only used for one locale project, for multi-locale,use hasShortcutByPackageName instead.
     * @param context
     * @param appNameID
     * @param fixChineseTitle
     * @return true if it exist
     */
    public static boolean hasShortcut(Context context,int appNameID,String fixChineseTitle) {
        String url = "content://" + getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS") + "/favorites?notify=true";
        ContentResolver resolver = context.getContentResolver();

        String appName;
        if (CommonUtilsEnv.SWITCHER_SHORTCUT_CHINESE_ALWASY){
            appName = fixChineseTitle;
        } else {
            appName = context.getString(appNameID);
        }
        Cursor cursor = resolver.query(Uri.parse(url), new String[]{"title", "iconResource"}, "title=?",
                new String[]{appName}, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            LogUtil.d("hasShortcut : true");
            return true;
        }
        LogUtil.d("hasShortcut : false");
        return false;
    }
    /**
     * Judge whether shortcut exist by packageName.for multi-locale.
     * @param context
     * @param appNameID
     * @param fixChineseTitle
     * @return true if it exist
     */
    public static boolean hasShortcutByPackageName(Context context) {
        if (context == null){
            return false;
        }
        String url = "";
        url = "content://" + getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS") + "/favorites?notify=true";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(url), new String[]{"title", "iconResource"}, "iconPackage=?",
                new String[]{context.getPackageName()}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            LogUtil.d("hasShortcutByPackageName : true");
            return true;
        }
        LogUtil.d("hasShortcutByPackageName : false");
        return false;
    }

    public static String getAuthorityFromPermission(Context context, String permission) {
        if (permission == null)
            return null;
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
/*                        LogUtil.d("getAuthorityFromPermission : " + provider.authority + " : " + provider.readPermission
                        + " : " + provider.writePermission);
                        if (permission.equals(provider.readPermission))
                            return provider.authority;
                        if (permission.equals(provider.writePermission))
                            return provider.authority;*/
                        if (permission.equals(provider.readPermission)){
                            return provider.authority;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get shortcut title at telephony.db, to remove the target package shortcut with different language
     * @param context
     * @return title name, null if not find
     */
    public static String getShortCutTitleByPackageName(Context context) {
        if (context == null){
            return null;
        }
        String url = "content://" + getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS") + "/favorites?notify=true";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(url), new String[]{"title", "iconResource"}, "iconPackage=?",
                new String[]{context.getPackageName()}, null);
        if (cursor != null && cursor.getCount() > 0) {
        	cursor.moveToFirst();
            String result = cursor.getString(cursor.getColumnIndex("title"));
            LogUtil.d("getShortCutTitleByPackageName : title is " + result);
            cursor.close();
            return result;
        }
        LogUtil.w("getShortCutTitleByPackageName : no title");
        return null;
    }
}