package com.gianlu.commonutils.Billing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlu.commonutils.R;

import java.util.List;

@SuppressWarnings("unused,WeakerAccess")
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products;
    private final IAdapter handler;
    private final LayoutInflater inflater;

    public ProductAdapter(Context context, List<Product> products, IAdapter handler) {
        this.products = products;
        this.handler = handler;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product item = products.get(position);

        switch (item.productId) {
            case "donation.lemonade":
                holder.icon.setImageResource(R.drawable.ic_lemonade_48dp);
                break;
            case "donation.coffee":
                holder.icon.setImageResource(R.drawable.ic_coffee_48dp);
                break;
            case "donation.hamburger":
                holder.icon.setImageResource(R.drawable.ic_cheese_burger_48dp);
                break;
            case "donation.pizza":
                holder.icon.setImageResource(R.drawable.ic_pizza_48dp);
                break;
            case "donation.sushi":
                holder.icon.setImageResource(R.drawable.ic_sushi_48dp);
                break;
            case "donation.champagne":
                holder.icon.setImageResource(R.drawable.ic_champagne_48dp);
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

    public interface IAdapter {
        void onItemSelected(Product product);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView icon;
        public final TextView title;
        final TextView description;
        final Button buy;

        ViewHolder(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.productItem_icon);
            title = itemView.findViewById(R.id.productItem_title);
            description = itemView.findViewById(R.id.productItem_description);
            buy = itemView.findViewById(R.id.productItem_buy);
            buy.setFocusable(false);
        }
    }
}
