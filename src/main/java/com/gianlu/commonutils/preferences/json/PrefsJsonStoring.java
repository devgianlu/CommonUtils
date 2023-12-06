package com.gianlu.commonutils.preferences.json;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gianlu.commonutils.preferences.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class PrefsJsonStoring extends JsonStoring {

    PrefsJsonStoring() {
    }

    @Nullable
    @Override
    public JSONObject getJsonObject(@NonNull String key) throws JSONException {
        String str = Prefs.getString(key, null);
        if (str == null) return null;
        str = new String(Base64.decode(str, Base64.NO_WRAP));
        return new JSONObject(str);
    }

    @Nullable
    @Override
    public JSONArray getJsonArray(@NonNull String key) throws JSONException {
        String str = Prefs.getString(key, null);
        if (str == null) return null;
        str = new String(Base64.decode(str, Base64.NO_WRAP));
        return new JSONArray(str);
    }

    @Override
    public void putJsonArray(@NonNull String key, JSONArray array) {
        if (array == null) {
            Prefs.remove(key);
            return;
        }

        String str = array.toString();
        str = Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
        Prefs.putString(key, str);
    }

    @Override
    public void putJsonObject(@NonNull String key, JSONObject obj) {
        if (obj == null) {
            Prefs.remove(key);
            return;
        }

        String str = obj.toString();
        str = Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
        Prefs.putString(key, str);
    }
}
