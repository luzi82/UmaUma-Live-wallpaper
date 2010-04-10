package com.luzi82.umawallpaper;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		// app ver
		Preference appVerPreference = findPreference("preference_info_about_version");
		PackageManager pm = getPackageManager();
		String pn = getPackageName();
		String appVerString = "unknown";
		try {
			PackageInfo pi = pm.getPackageInfo(pn, 0);
			appVerString = String.format("%1$s (%2$d)", pi.versionName,
					pi.versionCode);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		appVerPreference.setSummary(appVerString);

		// jni ver
		Preference jniVerPreference = findPreference("preference_info_about_lib_version");
		byte[] buf = new byte[15];
		LibUmaumaSo.getVersion(buf);
		String libVer = "unknown";
		try {
			libVer = new String(buf, "ASCII");
		} catch (UnsupportedEncodingException e) {
		}
		jniVerPreference.setSummary(libVer);

		// email intent
		Preference emailPreference = findPreference("preference_info_about_email");
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc882");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources()
				.getString(R.string.preference_info_about_email_address) });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(
				R.string.app_name));
		emailPreference.setIntent(Intent.createChooser(emailIntent,
				emailPreference.getTitle()));
	}

}
