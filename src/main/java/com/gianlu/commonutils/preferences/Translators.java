package com.gianlu.commonutils.preferences;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public final class Translators extends ArrayList<Translators.Item> {
    private static final String TAG = Translators.class.getSimpleName();
    private static Translators instance = null;

    private Translators() {
        super(0);
    }

    private Translators(@NonNull JSONArray array) throws JSONException {
        super(array.length());

        for (int i = 0; i < array.length(); i++)
            add(new Item(array.getJSONObject(i)));
    }

    @NonNull
    public static Translators load(@NonNull Context context) {
        if (instance != null) return instance;

        if (R.raw.translators == 0) return new Translators();

        try {
            JSONArray array = new JSONArray(CommonUtils.readEntirely(context.getResources().openRawResource(R.raw.translators)));
            return instance = new Translators(array);
        } catch (JSONException | IOException | RuntimeException ex) {
            Log.w(TAG, "Failed parsing translators!", ex);
            return new Translators();
        }
    }

    public static class Item {
        public final String name;
        public final String link;
        public final String languages;

        Item(@NonNull JSONObject obj) throws JSONException {
            name = obj.getString("name");
            link = obj.getString("link");
            languages = CommonUtils.join(obj.getJSONArray("languages"), ", ");
        }
    }
}
