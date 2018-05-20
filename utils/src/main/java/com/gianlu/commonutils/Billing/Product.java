package com.gianlu.commonutils.Billing;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    public final String productId;
    public final String price;
    public final String title;
    public final String type;
    public final String description;

    @SuppressWarnings("deprecation")
    Product(JSONObject obj) throws JSONException {
        productId = obj.getString("productId");
        price = obj.getString("price");
        title = obj.getString("title").replaceAll("\\s\\(.*\\)", "");
        type = obj.getString("type");
        description = Html.fromHtml(obj.getString("description")).toString();
    }
}
