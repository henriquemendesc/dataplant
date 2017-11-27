package com.parse.starter.persistence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bob on 22/11/2017.
 * salva as informações de usuário
 */

public class SaveSharedPreferences {
    public static final String LOGIN_PREFS_NAME = "LoginPrefsFile";
    public static final String USER_EMAIL = "usermail";
    public static final String USER_ID = "userid";
    public static final String USER_NAME = "username";
    public static final String USER_ADMIN = "useradmin";
    private static final String IS_CATALOG = "is_catalog";

    public static void setUserEmail(Context context, String userEmail){
        SharedPreferences.Editor editor =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(USER_EMAIL,userEmail);
        editor.commit();
    }
    public static String getUserEmail(Context context){
        SharedPreferences pref =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(USER_EMAIL, "");
    }

    public static void setUserId(Context context, String userId){
        SharedPreferences.Editor editor =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(USER_ID,userId);
        editor.commit();
    }
    public static String getUserId(Context context){
        SharedPreferences pref =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(USER_ID, "");
    }

    public static void setUserName(Context context, String userName){
        SharedPreferences.Editor editor =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(USER_NAME,userName);
        editor.commit();
    }
    public static String getUserName(Context context){
        SharedPreferences pref =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getString(USER_NAME, "");
    }

    public static void setUserAdmin(Context context, Boolean userAdmin){
        SharedPreferences.Editor editor =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(USER_ADMIN,userAdmin);
        editor.commit();
    }
    public static Boolean getUserAdmin(Context context){
        SharedPreferences pref =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(USER_ADMIN, false);
    }

    public static void setIsCatalog(Context context, Boolean isCatalog){
        SharedPreferences.Editor editor =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_CATALOG,isCatalog);
        editor.commit();
    }
    public static Boolean getIsCatalog(Context context){
        SharedPreferences pref =  context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_CATALOG, false);
    }
}
