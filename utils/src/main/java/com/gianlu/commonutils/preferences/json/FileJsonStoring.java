package com.gianlu.commonutils.preferences.json;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gianlu.commonutils.logging.Logging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

final class FileJsonStoring extends JsonStoring {
    private final File file;
    private final JSONObject obj;

    FileJsonStoring(@NonNull File file) {
        this.file = file;
        this.obj = load();
    }

    @NonNull
    private JSONObject load() {
        try (FileInputStream in = new FileInputStream(file)) {
            StringBuilder builder = new StringBuilder(in.available());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);

            return new JSONObject(builder.toString());
        } catch (IOException | JSONException ex) {
            Logging.log(ex);
            return new JSONObject();
        }
    }

    private void save() {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(obj.toString().getBytes());
        } catch (IOException ex) {
            Logging.log(ex);
        }
    }

    @Nullable
    @Override
    public JSONObject getJsonObject(@NonNull String key) throws JSONException {
        return obj.getJSONObject(key);
    }

    @Nullable
    @Override
    public JSONArray getJsonArray(@NonNull String key) throws JSONException {
        return obj.getJSONArray(key);
    }

    @Override
    public void putJsonArray(@NonNull String key, JSONArray array) throws JSONException {
        obj.put(key, array);
        save();
    }

    @Override
    public void putJsonObject(@NonNull String key, JSONObject obj) throws JSONException {
        this.obj.put(key, obj);
        save();
    }
}
