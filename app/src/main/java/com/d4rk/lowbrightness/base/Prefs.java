package com.d4rk.lowbrightness.base;
import android.content.Context;
import android.content.SharedPreferences;
public class Prefs {
	static public SharedPreferences get(Context c) {
		return c.getSharedPreferences("settings", Context.MODE_PRIVATE);
	}
}
