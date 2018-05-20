package com.gianlu.commonutils.Billing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlu.commonutils.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final Listener handler;
    private final LayoutInflater inflater;

    public ProductAdapter(Context context, List<Product> products, Listener handler) {
        this.products = products;
        this.handler = handler;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product item = products.get(position);

        switch (item.productId) {
            case "donation.lemonade":
                holder.icon.setImageResource(R.drawable.ic_juice_64dp);
                break;
            case "donation.coffee":
                holder.icon.setImageResource(R.drawable.ic_coffee_64dp);
                break;
            case "donation.hamburger":
                holder.icon.setImageResource(R.drawable.ic_hamburger_64dp);
                break;
            case "donation.pizza":
                holder.icon.setImageResource(R.drawable.ic_pizza_64dp);
                break;
            case "donation.sushi":
                holder.icon.setImageResource(R.drawable.ic_sushi_64dp);
                break;
            case "donation.champagne":
                holder.icon.setImageResource(R.drawable.ic_champagne_64dp);
                break;
        }

        holder.title.setText(item.title);
        holder.description.setText(item.description);
        holder.buy.setText(item.price);
        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (handler != null)
                    handler.onItemSelected(item);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.onItemSelected(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface Listener {
        void onItemSelected(@NonNull Product product);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView description;
        final Button buy;

        ViewHolder(ViewGroup parent) {
            super(inflater.inflate(R.layout.product_item, parent, false));

            icon = itemView.findViewById(R.id.productItem_icon);
            title = itemView.findViewById(R.id.productItem_title);
            description = itemView.findViewById(R.id.productItem_description);
            buy = itemView.findViewById(R.id.productItem_buy);
            buy.setFocusable(false);
        }
    }
}
