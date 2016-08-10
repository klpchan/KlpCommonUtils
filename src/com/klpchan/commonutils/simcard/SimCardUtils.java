package com.klpchan.commonutils.simcard;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.klpchan.commonutils.LogUtil;
import com.samsung.android.telephony.MultiSimManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by samsung on 2016/2/19.
 */
public class SimCardUtils {

    public static final boolean DEBUGMODE = false;

    static ArrayList<MccEntry> sTable;

    /**
     * Unknown network class. {@hide}
     */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks. {@hide}
     */
    public static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks. {@hide}
     */
    public static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks. {@hide}
     */
    public static final int NETWORK_CLASS_4_G = 3;

    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /**
     * Current network is GSM {@hide}
     */
    public static final int NETWORK_TYPE_GSM = 16;
//< RNTFIX::Support TDSCDMA
    /**
     * Current network is TD_SCDMA {@hide}
     */
    public static final int NETWORK_TYPE_TD_SCDMA = 17;
//> RNTFIX
//< RNTFIX
    /**
     * @hide
     */
    public static final int NETWORK_TYPE_IWLAN = 18;

    /**
     * @hide
     */
    public static final int NETWORK_TYPE_DC = 30;

    /**
     * @hide
     */
    public static final int NETWORK_TYPE_TDLTE = 31;

    public static final int NETWORK_MODE_WCDMA_PREF = 0; /* GSM/WCDMA (WCDMA preferred) */
    public static final int NETWORK_MODE_GSM_ONLY = 1; /* GSM only */
    public static final int NETWORK_MODE_WCDMA_ONLY = 2; /* WCDMA only */
    public static final int NETWORK_MODE_GSM_UMTS = 3; /* GSM/WCDMA (auto mode, according to PRL)
                                            AVAILABLE Application Settings menu*/
    public static final int NETWORK_MODE_CDMA = 4; /* CDMA and EvDo (auto mode, according to PRL)
                                            AVAILABLE Application Settings menu*/
    public static final int NETWORK_MODE_CDMA_NO_EVDO = 5; /* CDMA only */
    public static final int NETWORK_MODE_EVDO_NO_CDMA = 6; /* EvDo only */
    public static final int NETWORK_MODE_GLOBAL = 7; /* GSM/WCDMA, CDMA, and EvDo (auto mode, according to PRL)
                                            AVAILABLE Application Settings menu*/
    public static final int NETWORK_MODE_LTE_CDMA_EVDO = 8; /* LTE, CDMA and EvDo */
    public static final int NETWORK_MODE_LTE_GSM_WCDMA = 9; /* LTE, GSM/WCDMA */
    public static final int NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA = 10; /* LTE, CDMA, EvDo, GSM/WCDMA */
    public static final int NETWORK_MODE_LTE_ONLY = 11; /* LTE Only mode. */
    public static final int NETWORK_MODE_LTE_WCDMA = 12; /* LTE/WCDMA */

    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_GSM:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
//< RNTFIX::Support TDSCDMA
            case NETWORK_TYPE_TD_SCDMA:
//> RNTFIX
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
//< RNTFIX
            case NETWORK_TYPE_IWLAN:
//> RNTFIX
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }


    public static String getSalesCode() {
        String salesCode = SystemProperties.get("persist.omc.sales_code");
        if ("".equals(salesCode) || salesCode == null) {
            salesCode = SystemProperties.get("ro.csc.sales_code");
        }
        return salesCode;
    }

    public static boolean isChinaModel() {
        String salesCode = getSalesCode();
        return (salesCode != null && ("CHZ".equals(salesCode) || "CHN".equals(salesCode) || "CHM".equals(salesCode) || "CHU".equals(salesCode)
                || "CTC".equals(salesCode) || "CHC".equals(salesCode)));
    }

    public static boolean isCTCModel() {
        String salesCode = getSalesCode();
        return (salesCode != null && "CTC".equals(salesCode));
    }

    public static boolean isHKTWModel() {
        String salesCode = getSalesCode();
        return (salesCode != null && ("BRI".equals(salesCode) || "TGY".equals(salesCode)));
    }

    public static boolean isSlotReady(int slotID){
        boolean res;
        res = MultiSimManager.getSimState(slotID) == TelephonyManager.SIM_STATE_READY;
        return res;
    }

    public static boolean isSlotEmpty(int slotID){
        boolean res;
        res =  TelephonyManager.SIM_STATE_ABSENT == MultiSimManager.getSimState(slotID);
        return res;
    }

    public static String getRatString(String netType) {
        String[] mDataType = netType.split(":");
        //Log.e("qxy", "mDataType = " + mDataType[0]);
        if ("UMTS".equals(mDataType[0]) || "HSPA".equals(mDataType[0]) || "HSDPA".equals(mDataType[0]) || "HSUPA".equals(mDataType[0])
                || "HSPAP".equals(mDataType[0]) || "TD-SCDMA".equals(mDataType[0])) {
            return " 3G";
        } else if ("LTE".equals(mDataType[0])) {
            return " 4G";
        } else {
            return "";
        }
    }

    public static String NetworkModeDetailName(Context context, int NetworkType) {
        String name = "None";
        int resId = -1;
        boolean isNumberic = Locale.FRANCE.getLanguage().equals(Locale.getDefault().getLanguage()) || isChinaModel() || isHKTWModel();

        switch (NetworkType) {
            case NETWORK_MODE_LTE_GSM_WCDMA:
                resId = getResourceId(context, "lte_wcdma_gsm_automode", "string",
                        "com.android.phone");
                name = "LTE/WCDMA/GSM\n(auto connect)";
                if (isUSE_SIMPLE_NETWORK_WORD()) {
                    resId = getResourceId(context, "mode_lte_3g_2g_autoconnect", "string",
                            "com.android.phone");
                    name = "LTE/3G/2G (auto connect)";
                }
                if (isNumberic) {
                    resId = getResourceId(context, "mode_4g_3g_2g_autoconnect", "string",
                            "com.android.phone");
                    name = "4G/3G/2G (auto connect)";
                }
                break;
            case NETWORK_MODE_WCDMA_PREF:
                resId = getResourceId(context, "wcdma_gsm_automode", "string", "com.android.phone");
                name = "WCDMA/GSM\n(auto connect)";
                if (isUSE_SIMPLE_NETWORK_WORD() || isNumberic) {
                    resId = getResourceId(context, "mode_3g_2g_autoconnect", "string",
                            "com.android.phone");
                    name = "3G/2G (auto connect)";
                }
                break;
            case NETWORK_MODE_GSM_ONLY:
                resId = getResourceId(context, "gsm_only", "string", "com.android.phone");
                name = "GSM only";
                if (isUSE_SIMPLE_NETWORK_WORD() || isNumberic) {
                    resId = getResourceId(context, "mode_2g_only", "string", "com.android.phone");
                    name = "2G only";
                }
                break;
            case NETWORK_MODE_WCDMA_ONLY:
                resId = getResourceId(context, "wcdma_only", "string", "com.android.phone");
                name = "WCDMA only";
                if (isUSE_SIMPLE_NETWORK_WORD() || isNumberic) {
                    resId = getResourceId(context, "mode_3g_only", "string", "com.android.phone");
                    name = "3G only";
                }
                break;
            case NETWORK_MODE_LTE_ONLY:
                resId = getResourceId(context, "lte_only", "string", "com.android.phone");
                name = "LTE only";
                break;
            default:
                resId = getResourceId(context, "lte_wcdma_gsm_automode", "string", "com.android.phone");
                name = "LTE/WCDMA/GSM\n(auto connect)";
                if (isUSE_SIMPLE_NETWORK_WORD()) {
                    resId = getResourceId(context, "mode_lte_3g_2g_autoconnect", "string",
                            "com.android.phone");
                    name = "LTE/3G/2G (auto connect)";
                }
                if (isNumberic) {
                    resId = getResourceId(context, "mode_4g_3g_2g_autoconnect", "string",
                            "com.android.phone");
                    name = "4G/3G/2G (auto connect)";
                }
                break;
        }

        if (resId != -1) {
            try {
                Resources mResources = context.getPackageManager().getResourcesForApplication(
                        "com.android.phone");
                name = mResources.getString(resId);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return name;
    }

    public static String NetworkModeSimpleName(Context context, int NetworkType) {
        //Log.w(TAG, "PREFERRED_NETWORK_MODE : " + NetworkType);
        String name = "None";
        boolean isNumberic = Locale.FRANCE.getLanguage().equals(Locale.getDefault().getLanguage()) || isChinaModel() || isHKTWModel();

        switch (NetworkType) {
            case NETWORK_MODE_LTE_GSM_WCDMA:
                name = "LTE/WCDMA/GSM";
                if (isUSE_SIMPLE_NETWORK_WORD()) {
                    name = "LTE/3G/2G";
                }
                if (isNumberic) {
                    name = "4G/3G/2G";
                }
                break;
            case NETWORK_MODE_WCDMA_PREF:
                name = "WCDMA/GSM";
                if (isUSE_SIMPLE_NETWORK_WORD() || isNumberic) {
                    name = "3G/2G";
                }
                break;
            case NETWORK_MODE_GSM_ONLY:
                name = NetworkModeDetailName(context, NETWORK_MODE_GSM_ONLY);
                break;
            case NETWORK_MODE_WCDMA_ONLY:
                name = NetworkModeDetailName(context, NETWORK_MODE_WCDMA_ONLY);
                break;
            case NETWORK_MODE_LTE_ONLY:
                name = NetworkModeDetailName(context, NETWORK_MODE_LTE_ONLY);
                break;
            default:
                name = "LTE/WCDMA/GSM";
                if (isUSE_SIMPLE_NETWORK_WORD()) {
                    name = "LTE/3G/2G";
                }
                if (isNumberic) {
                    name = "4G/3G/2G";
                }
                break;
        }

        return name;
    }

    public static boolean isUSE_SIMPLE_NETWORK_WORD() {
        return false;//!("PHN".equals(salesCode) || "LUX".equals(salesCode)) || makeFeatureForCanada();
    }

    public static int getResourceId(Context context, String ResourceName, String defType, String defPackage) {
        Resources mResources = null;
        PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                mResources = pm.getResourcesForApplication(defPackage);
            } else {
                //Log.secE(TAG, "PackageManager is null!");
                return -1;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (mResources != null) {
            int id = mResources.getIdentifier(ResourceName, defType, defPackage);
            if (id != 0) {
                //Log.secW(TAG, " ResourceName: " + ResourceName + ", ID: " + id);
                return id;
            } else {
                //Log.secE(TAG, "Not find resource!");
                return -1;
            }
        } else {
            //Log.secE(TAG, "Resource is null!");
            return -1;
        }
    }

    public void setRadioChangeListener() {
//        NetAuthManager.
    }

    public static String getGSMOperatorNumeric(int simSlotID) {
        return MultiSimManager.getTelephonyProperty("gsm.operator.numeric", simSlotID, "");
    }

    public static int getMCCFromOperatorNumeric(String numeric) {
        if (numeric == null || numeric.length() < 3)
            return -1;
        return Integer.parseInt(numeric.substring(0, 3));
    }

    public static String getNetworkISOCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iso = manager.getNetworkCountryIso().toUpperCase();
        return iso;
    }

    static {
        sTable = new ArrayList<MccEntry>(240);


        /*
         * The table below is built from two resources:
         *
         * 1) ITU "Mobile Network Code (MNC) for the international
         *   identification plan for mobile terminals and mobile users"
         *   which is available as an annex to the ITU operational bulletin
         *   available here: http://www.itu.int/itu-t/bulletin/annex.html
         *
         * 2) The ISO 3166 country codes list, available here:
         *    http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/index.html
         *
         * This table has not been verified.
         */

        sTable.add(new MccEntry(202, "gr", 2));    //Greece
        sTable.add(new MccEntry(204, "nl", 2));    //Netherlands (Kingdom of the)
        sTable.add(new MccEntry(206, "be", 2));    //Belgium
        sTable.add(new MccEntry(208, "fr", 2));    //France
        sTable.add(new MccEntry(212, "mc", 2));    //Monaco (Principality of)
        sTable.add(new MccEntry(213, "ad", 2));    //Andorra (Principality of)
        sTable.add(new MccEntry(214, "es", 2));    //Spain
        sTable.add(new MccEntry(216, "hu", 2));    //Hungary (Republic of)
        sTable.add(new MccEntry(218, "ba", 2));    //Bosnia and Herzegovina
        sTable.add(new MccEntry(219, "hr", 2));    //Croatia (Republic of)
        sTable.add(new MccEntry(220, "rs", 2));    //Serbia and Montenegro
        sTable.add(new MccEntry(222, "it", 2));    //Italy
        sTable.add(new MccEntry(225, "va", 2));    //Vatican City State
        sTable.add(new MccEntry(226, "ro", 2));    //Romania
        sTable.add(new MccEntry(228, "ch", 2));    //Switzerland (Confederation of)
        sTable.add(new MccEntry(230, "cz", 2));    //Czech Republic
        sTable.add(new MccEntry(231, "sk", 2));    //Slovak Republic
        sTable.add(new MccEntry(232, "at", 2));    //Austria
        sTable.add(new MccEntry(234, "gb", 2));    //United Kingdom of Great Britain and Northern Ireland
        sTable.add(new MccEntry(235, "gb", 2));    //United Kingdom of Great Britain and Northern Ireland
        sTable.add(new MccEntry(238, "dk", 2));    //Denmark
        sTable.add(new MccEntry(240, "se", 2));    //Sweden
        sTable.add(new MccEntry(242, "no", 2));    //Norway
        sTable.add(new MccEntry(244, "fi", 2));    //Finland
        sTable.add(new MccEntry(246, "lt", 2));    //Lithuania (Republic of)
        sTable.add(new MccEntry(247, "lv", 2));    //Latvia (Republic of)
        sTable.add(new MccEntry(248, "ee", 2));    //Estonia (Republic of)
        sTable.add(new MccEntry(250, "ru", 2));    //Russian Federation
        sTable.add(new MccEntry(255, "ua", 2));    //Ukraine
        sTable.add(new MccEntry(257, "by", 2));    //Belarus (Republic of)
        sTable.add(new MccEntry(259, "md", 2));    //Moldova (Republic of)
        sTable.add(new MccEntry(260, "pl", 2));    //Poland (Republic of)
        sTable.add(new MccEntry(262, "de", 2));    //Germany (Federal Republic of)
        sTable.add(new MccEntry(266, "gi", 2));    //Gibraltar
        sTable.add(new MccEntry(268, "pt", 2));    //Portugal
        sTable.add(new MccEntry(270, "lu", 2));    //Luxembourg
        sTable.add(new MccEntry(272, "ie", 2));    //Ireland
        sTable.add(new MccEntry(274, "is", 2));    //Iceland
        sTable.add(new MccEntry(276, "al", 2));    //Albania (Republic of)
        sTable.add(new MccEntry(278, "mt", 2));    //Malta
        sTable.add(new MccEntry(280, "cy", 2));    //Cyprus (Republic of)
        sTable.add(new MccEntry(282, "ge", 2));    //Georgia
        sTable.add(new MccEntry(283, "am", 2));    //Armenia (Republic of)
        sTable.add(new MccEntry(284, "bg", 2));    //Bulgaria (Republic of)
        sTable.add(new MccEntry(286, "tr", 2));    //Turkey
        sTable.add(new MccEntry(288, "fo", 2));    //Faroe Islands
        sTable.add(new MccEntry(289, "ge", 2));    //Abkhazia (Georgia)
        sTable.add(new MccEntry(290, "gl", 2));    //Greenland (Denmark)
        sTable.add(new MccEntry(292, "sm", 2));    //San Marino (Republic of)
        sTable.add(new MccEntry(293, "si", 2));    //Slovenia (Republic of)
        sTable.add(new MccEntry(294, "mk", 2));   //The Former Yugoslav Republic of Macedonia
        sTable.add(new MccEntry(295, "li", 2));    //Liechtenstein (Principality of)
        sTable.add(new MccEntry(297, "me", 2));    //Montenegro (Republic of)
        sTable.add(new MccEntry(302, "ca", 3));    //Canada
        sTable.add(new MccEntry(308, "pm", 2));    //Saint Pierre and Miquelon (Collectivit territoriale de la Rpublique franaise)
        sTable.add(new MccEntry(310, "us", 3));    //United States of America
        sTable.add(new MccEntry(311, "us", 3));    //United States of America
        sTable.add(new MccEntry(312, "us", 3));    //United States of America
        sTable.add(new MccEntry(313, "us", 3));    //United States of America
        sTable.add(new MccEntry(314, "us", 3));    //United States of America
        sTable.add(new MccEntry(315, "us", 3));    //United States of America
        sTable.add(new MccEntry(316, "us", 3));    //United States of America
        sTable.add(new MccEntry(330, "pr", 2));    //Puerto Rico
        sTable.add(new MccEntry(332, "vi", 2));    //United States Virgin Islands
        sTable.add(new MccEntry(334, "mx", 3));    //Mexico
        sTable.add(new MccEntry(338, "jm", 3));    //Jamaica
        sTable.add(new MccEntry(340, "gp", 2));    //Guadeloupe (French Department of)
        sTable.add(new MccEntry(342, "bb", 3));    //Barbados
        sTable.add(new MccEntry(344, "ag", 3));    //Antigua and Barbuda
        sTable.add(new MccEntry(346, "ky", 3));    //Cayman Islands
        sTable.add(new MccEntry(348, "vg", 3));    //British Virgin Islands
        sTable.add(new MccEntry(350, "bm", 2));    //Bermuda
        sTable.add(new MccEntry(352, "gd", 2));    //Grenada
        sTable.add(new MccEntry(354, "ms", 2));    //Montserrat
        sTable.add(new MccEntry(356, "kn", 2));    //Saint Kitts and Nevis
        sTable.add(new MccEntry(358, "lc", 2));    //Saint Lucia
        sTable.add(new MccEntry(360, "vc", 2));    //Saint Vincent and the Grenadines
        sTable.add(new MccEntry(362, "ai", 2));    //Netherlands Antilles
        sTable.add(new MccEntry(363, "aw", 2));    //Aruba
        sTable.add(new MccEntry(364, "bs", 2));    //Bahamas (Commonwealth of the)
        sTable.add(new MccEntry(365, "ai", 3));    //Anguilla
        sTable.add(new MccEntry(366, "dm", 2));    //Dominica (Commonwealth of)
        sTable.add(new MccEntry(368, "cu", 2));    //Cuba
        sTable.add(new MccEntry(370, "do", 2));    //Dominican Republic
        sTable.add(new MccEntry(372, "ht", 2));    //Haiti (Republic of)
        sTable.add(new MccEntry(374, "tt", 2));    //Trinidad and Tobago
        sTable.add(new MccEntry(376, "tc", 2));    //Turks and Caicos Islands
        sTable.add(new MccEntry(400, "az", 2));    //Azerbaijani Republic
        sTable.add(new MccEntry(401, "kz", 2));    //Kazakhstan (Republic of)
        sTable.add(new MccEntry(402, "bt", 2));    //Bhutan (Kingdom of)
        sTable.add(new MccEntry(404, "in", 2));    //India (Republic of)
        sTable.add(new MccEntry(405, "in", 2));    //India (Republic of)
        sTable.add(new MccEntry(406, "in", 2));    //India (Republic of)
        sTable.add(new MccEntry(410, "pk", 2));    //Pakistan (Islamic Republic of)
        sTable.add(new MccEntry(412, "af", 2));    //Afghanistan
        sTable.add(new MccEntry(413, "lk", 2));    //Sri Lanka (Democratic Socialist Republic of)
        sTable.add(new MccEntry(414, "mm", 2));    //Myanmar (Union of)
        sTable.add(new MccEntry(415, "lb", 2));    //Lebanon
        sTable.add(new MccEntry(416, "jo", 2));    //Jordan (Hashemite Kingdom of)
        sTable.add(new MccEntry(417, "sy", 2));    //Syrian Arab Republic
        sTable.add(new MccEntry(418, "iq", 2));    //Iraq (Republic of)
        sTable.add(new MccEntry(419, "kw", 2));    //Kuwait (State of)
        sTable.add(new MccEntry(420, "sa", 2));    //Saudi Arabia (Kingdom of)
        sTable.add(new MccEntry(421, "ye", 2));    //Yemen (Republic of)
        sTable.add(new MccEntry(422, "om", 2));    //Oman (Sultanate of)
        sTable.add(new MccEntry(423, "ps", 2));    //Palestine
        sTable.add(new MccEntry(424, "ae", 2));    //United Arab Emirates
        sTable.add(new MccEntry(425, "il", 2));    //Israel (State of)
        sTable.add(new MccEntry(426, "bh", 2));    //Bahrain (Kingdom of)
        sTable.add(new MccEntry(427, "qa", 2));    //Qatar (State of)
        sTable.add(new MccEntry(428, "mn", 2));    //Mongolia
        sTable.add(new MccEntry(429, "np", 2));    //Nepal
        sTable.add(new MccEntry(430, "ae", 2));    //United Arab Emirates
        sTable.add(new MccEntry(431, "ae", 2));    //United Arab Emirates
        sTable.add(new MccEntry(432, "ir", 2));    //Iran (Islamic Republic of)
        sTable.add(new MccEntry(434, "uz", 2));    //Uzbekistan (Republic of)
        sTable.add(new MccEntry(436, "tj", 2));    //Tajikistan (Republic of)
        sTable.add(new MccEntry(437, "kg", 2));    //Kyrgyz Republic
        sTable.add(new MccEntry(438, "tm", 2));    //Turkmenistan
        sTable.add(new MccEntry(440, "jp", 2));    //Japan
        sTable.add(new MccEntry(441, "jp", 2));    //Japan
        sTable.add(new MccEntry(450, "kr", 2));    //Korea (Republic of)
        sTable.add(new MccEntry(452, "vn", 2));    //Viet Nam (Socialist Republic of)
        sTable.add(new MccEntry(454, "hk", 2));    //"Hong Kong, China"
        sTable.add(new MccEntry(455, "mo", 2));    //"Macao, China"
        sTable.add(new MccEntry(456, "kh", 2));    //Cambodia (Kingdom of)
        sTable.add(new MccEntry(457, "la", 2));    //Lao People's Democratic Republic
        sTable.add(new MccEntry(460, "cn", 2));    //China (People's Republic of)
        sTable.add(new MccEntry(461, "cn", 2));    //China (People's Republic of)
        sTable.add(new MccEntry(466, "tw", 2));    //"Taiwan, China"
        sTable.add(new MccEntry(467, "kp", 2));    //Democratic People's Republic of Korea
        sTable.add(new MccEntry(470, "bd", 2));    //Bangladesh (People's Republic of)
        sTable.add(new MccEntry(472, "mv", 2));    //Maldives (Republic of)
        sTable.add(new MccEntry(502, "my", 2));    //Malaysia
        sTable.add(new MccEntry(505, "au", 2));    //Australia
        sTable.add(new MccEntry(510, "id", 2));    //Indonesia (Republic of)
        sTable.add(new MccEntry(514, "tl", 2));    //Democratic Republic of Timor-Leste
        sTable.add(new MccEntry(515, "ph", 2));    //Philippines (Republic of the)
        sTable.add(new MccEntry(520, "th", 2));    //Thailand
        sTable.add(new MccEntry(525, "sg", 2));    //Singapore (Republic of)
        sTable.add(new MccEntry(528, "bn", 2));    //Brunei Darussalam
        sTable.add(new MccEntry(530, "nz", 2));    //New Zealand
        sTable.add(new MccEntry(534, "mp", 2));    //Northern Mariana Islands (Commonwealth of the)
        sTable.add(new MccEntry(535, "gu", 2));    //Guam
        sTable.add(new MccEntry(536, "nr", 2));    //Nauru (Republic of)
        sTable.add(new MccEntry(537, "pg", 2));    //Papua New Guinea
        sTable.add(new MccEntry(539, "to", 2));    //Tonga (Kingdom of)
        sTable.add(new MccEntry(540, "sb", 2));    //Solomon Islands
        sTable.add(new MccEntry(541, "vu", 2));    //Vanuatu (Republic of)
        sTable.add(new MccEntry(542, "fj", 2));    //Fiji (Republic of)
        sTable.add(new MccEntry(543, "wf", 2));    //Wallis and Futuna (Territoire franais d'outre-mer)
        sTable.add(new MccEntry(544, "as", 2));    //American Samoa
        sTable.add(new MccEntry(545, "ki", 2));    //Kiribati (Republic of)
        sTable.add(new MccEntry(546, "nc", 2));    //New Caledonia (Territoire franais d'outre-mer)
        sTable.add(new MccEntry(547, "pf", 2));    //French Polynesia (Territoire franais d'outre-mer)
        sTable.add(new MccEntry(548, "ck", 2));    //Cook Islands
        sTable.add(new MccEntry(549, "ws", 2));    //Samoa (Independent State of)
        sTable.add(new MccEntry(550, "fm", 2));    //Micronesia (Federated States of)
        sTable.add(new MccEntry(551, "mh", 2));    //Marshall Islands (Republic of the)
        sTable.add(new MccEntry(552, "pw", 2));    //Palau (Republic of)
        sTable.add(new MccEntry(553, "tv", 2));    //Tuvalu
        sTable.add(new MccEntry(555, "nu", 2));    //Niue
        sTable.add(new MccEntry(602, "eg", 2));    //Egypt (Arab Republic of)
        sTable.add(new MccEntry(603, "dz", 2));    //Algeria (People's Democratic Republic of)
        sTable.add(new MccEntry(604, "ma", 2));    //Morocco (Kingdom of)
        sTable.add(new MccEntry(605, "tn", 2));    //Tunisia
        sTable.add(new MccEntry(606, "ly", 2));    //Libya (Socialist People's Libyan Arab Jamahiriya)
        sTable.add(new MccEntry(607, "gm", 2));    //Gambia (Republic of the)
        sTable.add(new MccEntry(608, "sn", 2));    //Senegal (Republic of)
        sTable.add(new MccEntry(609, "mr", 2));    //Mauritania (Islamic Republic of)
        sTable.add(new MccEntry(610, "ml", 2));    //Mali (Republic of)
        sTable.add(new MccEntry(611, "gn", 2));    //Guinea (Republic of)
        sTable.add(new MccEntry(612, "ci", 2));    //Cote d'Ivoire (Republic of)
        sTable.add(new MccEntry(613, "bf", 2));    //Burkina Faso
        sTable.add(new MccEntry(614, "ne", 2));    //Niger (Republic of the)
        sTable.add(new MccEntry(615, "tg", 2));    //Togolese Republic
        sTable.add(new MccEntry(616, "bj", 2));    //Benin (Republic of)
        sTable.add(new MccEntry(617, "mu", 2));    //Mauritius (Republic of)
        sTable.add(new MccEntry(618, "lr", 2));    //Liberia (Republic of)
        sTable.add(new MccEntry(619, "sl", 2));    //Sierra Leone
        sTable.add(new MccEntry(620, "gh", 2));    //Ghana
        sTable.add(new MccEntry(621, "ng", 2));    //Nigeria (Federal Republic of)
        sTable.add(new MccEntry(622, "td", 2));    //Chad (Republic of)
        sTable.add(new MccEntry(623, "cf", 2));    //Central African Republic
        sTable.add(new MccEntry(624, "cm", 2));    //Cameroon (Republic of)
        sTable.add(new MccEntry(625, "cv", 2));    //Cape Verde (Republic of)
        sTable.add(new MccEntry(626, "st", 2));    //Sao Tome and Principe (Democratic Republic of)
        sTable.add(new MccEntry(627, "gq", 2));    //Equatorial Guinea (Republic of)
        sTable.add(new MccEntry(628, "ga", 2));    //Gabonese Republic
        sTable.add(new MccEntry(629, "cg", 2));    //Congo (Republic of the)
        sTable.add(new MccEntry(630, "cg", 2));    //Democratic Republic of the Congo
        sTable.add(new MccEntry(631, "ao", 2));    //Angola (Republic of)
        sTable.add(new MccEntry(632, "gw", 2));    //Guinea-Bissau (Republic of)
        sTable.add(new MccEntry(633, "sc", 2));    //Seychelles (Republic of)
        sTable.add(new MccEntry(634, "sd", 2));    //Sudan (Republic of the)
        sTable.add(new MccEntry(635, "rw", 2));    //Rwanda (Republic of)
        sTable.add(new MccEntry(636, "et", 2));    //Ethiopia (Federal Democratic Republic of)
        sTable.add(new MccEntry(637, "so", 2));    //Somali Democratic Republic
        sTable.add(new MccEntry(638, "dj", 2));    //Djibouti (Republic of)
        sTable.add(new MccEntry(639, "ke", 2));    //Kenya (Republic of)
        sTable.add(new MccEntry(640, "tz", 2));    //Tanzania (United Republic of)
        sTable.add(new MccEntry(641, "ug", 2));    //Uganda (Republic of)
        sTable.add(new MccEntry(642, "bi", 2));    //Burundi (Republic of)
        sTable.add(new MccEntry(643, "mz", 2));    //Mozambique (Republic of)
        sTable.add(new MccEntry(645, "zm", 2));    //Zambia (Republic of)
        sTable.add(new MccEntry(646, "mg", 2));    //Madagascar (Republic of)
        sTable.add(new MccEntry(647, "re", 2));    //Reunion (French Department of)
        sTable.add(new MccEntry(648, "zw", 2));    //Zimbabwe (Republic of)
        sTable.add(new MccEntry(649, "na", 2));    //Namibia (Republic of)
        sTable.add(new MccEntry(650, "mw", 2));    //Malawi
        sTable.add(new MccEntry(651, "ls", 2));    //Lesotho (Kingdom of)
        sTable.add(new MccEntry(652, "bw", 2));    //Botswana (Republic of)
        sTable.add(new MccEntry(653, "sz", 2));    //Swaziland (Kingdom of)
        sTable.add(new MccEntry(654, "km", 2));    //Comoros (Union of the)
        sTable.add(new MccEntry(655, "za", 2));    //South Africa (Republic of)
        sTable.add(new MccEntry(657, "er", 2));    //Eritrea
        sTable.add(new MccEntry(658, "sh", 2));    //Saint Helena, Ascension and Tristan da Cunha
        sTable.add(new MccEntry(659, "ss", 2));    //South Sudan (Republic of)
        sTable.add(new MccEntry(702, "bz", 2));    //Belize
        sTable.add(new MccEntry(704, "gt", 2));    //Guatemala (Republic of)
        sTable.add(new MccEntry(706, "sv", 2));    //El Salvador (Republic of)
        sTable.add(new MccEntry(708, "hn", 3));    //Honduras (Republic of)
        sTable.add(new MccEntry(710, "ni", 2));    //Nicaragua
        sTable.add(new MccEntry(712, "cr", 2));    //Costa Rica
        sTable.add(new MccEntry(714, "pa", 2));    //Panama (Republic of)
        sTable.add(new MccEntry(716, "pe", 2));    //Peru
        sTable.add(new MccEntry(722, "ar", 3));    //Argentine Republic
        sTable.add(new MccEntry(724, "br", 2));    //Brazil (Federative Republic of)
        sTable.add(new MccEntry(730, "cl", 2));    //Chile
        sTable.add(new MccEntry(732, "co", 3));    //Colombia (Republic of)
        sTable.add(new MccEntry(734, "ve", 2));    //Venezuela (Bolivarian Republic of)
        sTable.add(new MccEntry(736, "bo", 2));    //Bolivia (Republic of)
        sTable.add(new MccEntry(738, "gy", 2));    //Guyana
        sTable.add(new MccEntry(740, "ec", 2));    //Ecuador
        sTable.add(new MccEntry(742, "gf", 2));    //French Guiana (French Department of)
        sTable.add(new MccEntry(744, "py", 2));    //Paraguay (Republic of)
        sTable.add(new MccEntry(746, "sr", 2));    //Suriname (Republic of)
        sTable.add(new MccEntry(748, "uy", 2));    //Uruguay (Eastern Republic of)
        sTable.add(new MccEntry(750, "fk", 2));    //Falkland Islands (Malvinas)
        //table.add(new MccEntry(901,"",2));	//"International Mobile, shared code"
//< RNTFIX::SEC_PRODUCT_FEATURE_RIL_CHECK_MCCC
        sTable.add(new MccEntry(-1, "", 0));
//> RNTFIX

        Collections.sort(sTable);
    }

    static class MccEntry implements Comparable<MccEntry> {
        final int mMcc;
        final String mIso;
        final int mSmallestDigitsMnc;

        MccEntry(int mnc, String iso, int smallestDigitsMCC) {
            if (iso == null) {
                throw new NullPointerException();
            }
            mMcc = mnc;
            mIso = iso;
            mSmallestDigitsMnc = smallestDigitsMCC;
        }

        @Override
        public int compareTo(MccEntry o) {
            return mMcc - o.mMcc;
        }
    }

    private static MccEntry entryForMcc(int mcc) {
        MccEntry m = new MccEntry(mcc, "", 0);

        int index = Collections.binarySearch(sTable, m);

        if (index < 0) {
            return null;
        } else {
            return sTable.get(index);
        }
    }

    /**
     * Given a GSM Mobile Country Code, returns
     * an ISO two-character country code if available.
     * Returns "" if unavailable.
     */
    public static String countryCodeForMcc(int mcc) {
        MccEntry entry = entryForMcc(mcc);

        if (entry == null) {
            return "";
        } else {
            return entry.mIso;
        }
    }

    /**
     * Set Default Data Card Slot to simID
     * @param context
     * @param simId
     */
    public static void switchNetworkTo(final Context context, final int simId) {
        if (context == null || simId < 0 || simId > 1){
            LogUtil.e("switchNetworkTo fail context is " + context + " simId is " + simId);
            return;
        }

        if (!SimCardUtils.isSlotReady(simId)){
            LogUtil.e("switchNetworkTo fail sim not ready");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("switchNetworkTo slotid is  " + simId);

/*                SubscriptionManager subscriptionManager = (SubscriptionManager)context.getSystemService(
                        Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(
                        Context.TELEPHONY_SERVICE);*/
                if (isCTCModel()) {
                    LogUtil.d("switchNetworkTo(CTC):" + simId);
                    long[] subId = SimCardUtils.getSubId(simId);
                    if (subId != null) {
                        setDefaultDataSubId(context, (int)subId[0]);
                    }
                    Settings.System.putInt(context.getContentResolver(), "prefer_data_id", simId);
                } else {
                    int subId = MultiSimManager.getDefaultSubscriptionId(MultiSimManager.TYPE_DATA);
                    if (isValidSubscriptionId(context,subId)) {
                        LogUtil.d("switchNetworkTo(" + simId + ") - isValidSubscriptionId(" + subId + ") true");
//                telephonyManager.setDataEnabled(false);
                        setDataEnabled(context,false);
                    } else {
                        subId = MultiSimManager.getDefaultSubscriptionId(MultiSimManager.TYPE_DEFAULT);
                        LogUtil.d("switchNetworkTo(" + simId + ") - isValidSubscriptionId(" + subId + ") false");
//                telephonyManager.setDataEnabled(subId, false);
                        setDataEnabled(context,subId,false);
                    }
//            MultiSimManager.setDataEnabled(false);

//            SubscriptionInfo info = subscriptionManager.getActiveSubscriptionInfo(simId);

                    int time = 0;
                    while (time++ < 3){
                        try {
                            long[] targetId = getSubId(simId);
                            int targetsubid = 0;
                            if (targetId != null) {
                                targetsubid = (int)targetId[0];
                            }
                            LogUtil.d(String.format("switchNetworkTo : %s try to get target subid is " + targetsubid,time));
                            if (targetsubid < 65535 && targetsubid > 0){
                                LogUtil.d("switchNetworkTo : valid target subid " + targetsubid);
                                setDefaultDataSubId(context, targetsubid );
                                break;
                            }
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //add for store default previous default data slot id.
/*            try {
                int previousSlot = Settings.System.getInt(context.getContentResolver(), "prefer_data_id");
                if (!VirtualImsiUtils.isSlotVirtualSim(previousSlot)){
                    PreferencesUtils.putInt(context,CommonUtilsEnv.SP_KEY_PREVIOUS_DEFAULT_DATA_SLOT,previousSlot);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }*/

                    setDataEnabled(context,true);
//            Settings.System.putInt(context.getContentResolver(), "prefer_data_id", simId);
                    //telephonyManager.setDataEnabled(true);
//            MultiSimManager.setDataEnabled(true);
                }

                //LogUtil.d("call switchToNetwork3G, simId is " + simId);
                //NetworkUtil mNetworkUtil = new NetworkUtil(context);
                //mNetworkUtil.switchToNetwork3G(context,simId);
            }
        }).start();
    }

    public static boolean isValidSubscriptionId(Context context, int subId) {
        try {
            SubscriptionManager subscriptionManager = (SubscriptionManager)context.getSystemService(
                    Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            Class<?> cl = Class.forName("android.telephony.SubscriptionManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = { Integer.TYPE};
            Method method = cl.getDeclaredMethod("isValidSubscriptionId", paramTypes);
            return (boolean)method.invoke(subscriptionManager, subId);
        } catch (Exception e) {
            Log.v(TAG, "failed to call setDefaultDataSubId");
            e.printStackTrace();
        }
        return false;
    }

    public static void setDefaultDataSubId(Context context, int subId) {
        LogUtil.d("setDefaultDataSubId to subid is " + subId);
        try {
            SubscriptionManager subscriptionManager = (SubscriptionManager)context.getSystemService(
                    Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            Class<?> cl = Class.forName("android.telephony.SubscriptionManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = { Integer.TYPE};
            Method method = cl.getDeclaredMethod("setDefaultDataSubId", paramTypes);
            method.invoke(subscriptionManager, subId);
        } catch (Exception e) {
            Log.v(TAG, "failed to call setDefaultDataSubId");
            e.printStackTrace();
        }
    }

    public static long[] getSubId(int slotId) {
        long[] result = null;
        try {
            Class<?> cl = Class.forName("com.samsung.android.telephony.MultiSimManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = { Integer.TYPE };
            Method method = cl.getDeclaredMethod("getSubId", paramTypes);
            method.setAccessible(true);
            result = (long[]) method.invoke(null, slotId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getPhoneId(int slotId) {
        int result = -1;
        try {
            Class<?> cl = Class.forName("android.telephony.SubscriptionManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = { Integer.TYPE };
            Method method = cl.getDeclaredMethod("getPhoneId", paramTypes);
            method.setAccessible(true);
            result = (int) method.invoke(null, slotId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getTelephonyProperty(int slot, String property, String defaultVal) {
        String propVal = null;
        try {
            long[] subId = SimCardUtils.getSubId(slot - 1);
            int phoneId = -1;
            if(subId != null) {
                phoneId = SimCardUtils.getPhoneId((int) subId[0]);
            }

            Class<?> cl = Class.forName("android.telephony.TelephonyManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = { Integer.TYPE, String.class, String.class };
            Method method = cl.getDeclaredMethod("getTelephonyProperty", paramTypes);
            method.setAccessible(true);
            propVal = (String) method.invoke(null, phoneId, property, defaultVal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return propVal;
    }

    private static final String SOFTSIM_PROP_STATUS = "persist.sys.softsim.status";
    public static String getSoftSimStatusProp() {
        return SystemProperties.get(SOFTSIM_PROP_STATUS, null);
    }

/*    public static void clearSoftSimStatusProp() {
        SystemProperties.set(SOFTSIM_PROP_STATUS, SIMStateManager.SOFTSIM_STATUS_DEFAULT);
    }*/

    public static void setDataEnabled(Context context,boolean enable) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            Class<?> cl = Class.forName("android.telephony.TelephonyManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = { Boolean.TYPE };
            Method method = cl.getDeclaredMethod("setDataEnabled", paramTypes);
            method.invoke(telephonyManager, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDataEnabled(Context context,int subid,boolean enable) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            Class<?> cl = Class.forName("android.telephony.TelephonyManager");
            @SuppressWarnings("rawtypes")
			Class[] paramTypes = {Integer.TYPE,Boolean.TYPE };
            Method method = cl.getDeclaredMethod("setDataEnabled", paramTypes);
            method.invoke(telephonyManager, subid,enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = LogUtil.customTagPrefix + ":SimCardUtils";
    private static final Uri DEFAULTAPN_URI_USING_SUBID = Uri.parse("content://telephony/carriers/restore/subId/");
    public static final String RESTORE_CARRIERS_URI =
            "content://telephony/carriers/restore";
    private static final Uri DEFAULTAPN_URI = Uri.parse(RESTORE_CARRIERS_URI);

    /**
     *
     * @param context
     * @param simSlot 0 or 1.
     */
    @SuppressWarnings("deprecation")
	public static boolean restoreAPNSettings(Context context, int simSlot) {
        if (context == null) {
            Log.v(TAG, "restoreAPNSettings, bad params");
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        if (MultiSimManager.getSimSlotCount() > 1 && MultiSimManager.getActiveSubInfoCount() > 1) {
            int[] subId = MultiSimManager.getSubscriptionId(simSlot);
            if (subId == null) {
                return false;
            }

            int count = resolver.delete(Uri.parse(DEFAULTAPN_URI_USING_SUBID.toString() + subId[0]), null, null);
            Log.v(TAG, "SIM" + simSlot + " RESTORE : " + count);
        } else {
            resolver.delete(DEFAULTAPN_URI, null, null);
        }
        return true;
    }

/*    public static void setSystemSettingDB(Context context,int targetVirtualSlot){
        if ( context == null){
            LogUtil.e("setSystemSettingDB fail, context is null");
            return;
        }
        String PHONE1_VIRTUALSIM = "phone1_virtualsim"; //used for Settings.System.PHONE1_VIRTUALSIM
        String PHONE2_VIRTUALSIM = "phone2_virtualsim";

        Settings.System.putInt(context.getContentResolver(), PHONE1_VIRTUALSIM, targetVirtualSlot == 0 ? 1 : 0);
        Settings.System.putInt(context.getContentResolver(), PHONE2_VIRTUALSIM, targetVirtualSlot == 1 ? 1 : 0);
    }*/

    /**
     * @param context
     * @return 1 or 2, if it be used for slot id, it should minus 1
     */
/*    public static int getCurrentVirtualSlot(Context context){
        int slot = -1;
        OrderSoftSimInfoUtils orderSoftSimInfoUtils = OrderSoftSimInfoUtils.getInstance(context);
        SoftsimAdapter softsimAdapter = ((MainApplication)context.getApplicationContext()).getSoftsimAdapter();
        OrderSoftSimInfoUtils.OrderSoftSimInfo curActInfo = orderSoftSimInfoUtils.getCurActiveOrderSoftSimInfo(softsimAdapter);
        if (curActInfo != null) {
            slot = curActInfo.mSimSlot;
        }
        return slot;
    }*/
}
