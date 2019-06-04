/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.androidauto;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Process;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import code.name.monkey.retromusic.R;

public class PackageValidator {
    private static final String TAG = "PackageValidator";

    /**
     * Map allowed callers' certificate keys to the expected caller information.
     */
    private final Map<String, ArrayList<CallerInfo>> mValidCertificates;

    public PackageValidator(@NonNull Context context) {
        mValidCertificates = readValidCertificates(context.getResources().getXml(
                R.xml.allowed_media_browser_callers));
    }

    private Map<String, ArrayList<CallerInfo>> readValidCertificates(XmlResourceParser parser) {
        HashMap<String, ArrayList<CallerInfo>> validCertificates = new HashMap<>();
        try {
            int eventType = parser.next();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG
                        && parser.getName().equals("signing_certificate")) {

                    String name = parser.getAttributeValue(null, "name");
                    String packageName = parser.getAttributeValue(null, "package");
                    boolean isRelease = parser.getAttributeBooleanValue(null, "release", false);
                    String certificate = parser.nextText().replaceAll("\\s|\\n", "");

                    CallerInfo info = new CallerInfo(name, packageName, isRelease);

                    ArrayList<CallerInfo> infos = validCertificates.get(certificate);
                    if (infos == null) {
                        infos = new ArrayList<>();
                        validCertificates.put(certificate, infos);
                    }
                    Log.v(TAG, String.format("Adding allowed caller: %s package=%s release=%s certificate=%s", info.name, info.packageName, info.release, certificate));
                    infos.add(info);
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, String.format("%s Could not read allowed callers from XML.", e));
        }
        return validCertificates;
    }

    /**
     * @return false if the caller is not authorized to get data from this MediaBrowserService
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCallerAllowed(Context context, String callingPackage, int callingUid) {
        // Always allow calls from the framework, self app or development environment.
        if (Process.SYSTEM_UID == callingUid || Process.myUid() == callingUid) {
            return true;
        }

        if (isPlatformSigned(context, callingPackage)) {
            return true;
        }

        PackageInfo packageInfo = getPackageInfo(context, callingPackage);
        if (packageInfo == null) {
            return false;
        }
        if (packageInfo.signatures.length != 1) {
            Log.w(TAG, "Caller does not have exactly one signature certificate!");
            return false;
        }
        String signature = Base64.encodeToString(
                packageInfo.signatures[0].toByteArray(), Base64.NO_WRAP);

        // Test for known signatures:
        ArrayList<CallerInfo> validCallers = mValidCertificates.get(signature);
        if (validCallers == null) {
            Log.v(TAG, "Signature for caller " + callingPackage + " is not valid: \n" + signature);
            if (mValidCertificates.isEmpty()) {
                Log.w(TAG, String.format(
                        "The list of valid certificates is empty. Either your file res/xml/allowed_media_browser_callers.xml is empty or there was an error while reading it. Check previous log messages."));
            }
            return false;
        }

        // Check if the package name is valid for the certificate:
        StringBuffer expectedPackages = new StringBuffer();
        for (CallerInfo info : validCallers) {
            if (callingPackage.equals(info.packageName)) {
                Log.v(TAG, String.format("Valid caller: %s  package=%s release=%s", info.name, info.packageName, info.release));
                return true;
            }
            expectedPackages.append(info.packageName).append(' ');
        }

        Log.i(TAG, String.format(
                "Caller has a valid certificate, but its package doesn't match any expected package for the given certificate. Caller's package is %s. Expected packages as defined in res/xml/allowed_media_browser_callers.xml are (%s). This caller's certificate is: \n%s",
                callingPackage, expectedPackages, signature));

        return false;
    }

    /**
     * @return true if the installed package signature matches the platform signature.
     */
    private boolean isPlatformSigned(Context context, String pkgName) {
        PackageInfo platformPackageInfo = getPackageInfo(context, "android");

        // Should never happen.
        if (platformPackageInfo == null || platformPackageInfo.signatures == null
                || platformPackageInfo.signatures.length == 0) {
            return false;
        }

        PackageInfo clientPackageInfo = getPackageInfo(context, pkgName);

        return (clientPackageInfo != null && clientPackageInfo.signatures != null
                && clientPackageInfo.signatures.length > 0 &&
                platformPackageInfo.signatures[0].equals(clientPackageInfo.signatures[0]));
    }

    /**
     * @return {@link PackageInfo} for the package name or null if it's not found.
     */
    private PackageInfo getPackageInfo(Context context, String pkgName) {
        try {
            final PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.format("%s Package manager can't find package: %s", e, pkgName));
        }
        return null;
    }

    private final static class CallerInfo {
        final String name;
        final String packageName;
        final boolean release;

        public CallerInfo(String name, String packageName, boolean release) {
            this.name = name;
            this.packageName = packageName;
            this.release = release;
        }
    }
}
