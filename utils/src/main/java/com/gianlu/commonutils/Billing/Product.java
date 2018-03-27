package com.gianlu.commonutils.Billing;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    public String productId;
    public String price;
    public String title;
    public String type;
    public String description;

    @SuppressWarnings("deprecation")
    public Product(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        productId = obj.getString("productId");
        price = obj.getString("price");
        title = obj.getString("title").replaceAll("\\s\\(.*\\)", "");
        type = obj.getString("type");
        description = Html.fromHtml(obj.getString("description")).toString();
    }
}
