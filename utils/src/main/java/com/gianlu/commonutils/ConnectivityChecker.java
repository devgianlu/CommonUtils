package com.gianlu.commonutils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

@SuppressWarnings("unused")
public class ConnectivityChecker {
    private static URLProvider provider;
    private static String userAgent = "@devgianlu connectivity test script";

    public static void setProvider(URLProvider provider) {
        ConnectivityChecker.provider = provider;
    }

    public static void setUserAgent(String userAgent) {
        ConnectivityChecker.userAgent = userAgent;
    }

    public static boolean checkSync(@Nullable Context context) {
        return provider != null && checkInternal(context, false);
    }

    private static boolean checkInternal(@Nullable Context context, boolean shouldTryDotCom) {
        if (provider == null) provider = new GoogleURLProvider();

        try {
            HttpURLConnection conn = (HttpURLConnection) provider.getUrl(shouldTryDotCom).openConnection();
            conn.setRequestProperty("User-Agent", userAgent);
            conn.setRequestProperty("Connection", "close");
            conn.setConnectTimeout(2000);
            conn.connect();
            return provider.validateResponse(conn);
        } catch (IOException ex) {
            Logging.logMe(ex);
            return !shouldTryDotCom && checkInternal(context, true);
        }
    }

    public interface URLProvider {
        URL getUrl(boolean useDotCom) throws MalformedURLException;

        boolean validateResponse(HttpURLConnection connection) throws IOException;
    }

    public static class GoogleURLProvider implements URLProvider {

        private String pickCountryURL(boolean useDotCom) {
            String country = Locale.getDefault().getCountry();
            if (useDotCom || country.isEmpty() || country.length() >= 3)
                return "http://www.google.com/generate_204";
            else return "http://www.google." + country.toLowerCase() + "/generate_204";
        }

        @Override
        public URL getUrl(boolean useDotCom) throws MalformedURLException {
            return new URL(pickCountryURL(useDotCom));
        }

        @Override
        public boolean validateResponse(HttpURLConnection conn) throws IOException {
            return conn.getResponseCode() == 204 && conn.getContentLength() == 0;
        }
    }
}
