package com.cashrich.coinrich;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cashrich.coinrich.vo.MarketDataResponseVo;

import java.util.List;

public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.CoinsViewHolder> {

    private List<MarketDataResponseVo> itemList;  // List of items to display

    public CoinsAdapter(List<MarketDataResponseVo> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CoinsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coins_layout, parent, false);
        return new CoinsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinsViewHolder holder, int position) {
        // Bind data to the ViewHolder
        MarketDataResponseVo item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class CoinsViewHolder extends RecyclerView.ViewHolder {
        private TextView symbolText;
        private TextView rankText;
        private TextView rankField;
        private TextView volume_24hrText;
        private TextView volume_24hrField;
        private TextView priceText;
        private TextView priceField;

        public CoinsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views within the item layout
            symbolText = itemView.findViewById(R.id.symbol);
            rankText = itemView.findViewById(R.id.rankText);
            rankField = itemView.findViewById(R.id.rankField);
            volume_24hrText = itemView.findViewById(R.id.volume_24hrText);
            volume_24hrField = itemView.findViewById(R.id.volume_24hrField);
            priceText = itemView.findViewById(R.id.priceText);
            priceField = itemView.findViewById(R.id.priceField);
        }

        public void bind(MarketDataResponseVo item) {
            // Bind data to the views
            symbolText.setText(item.getSymbol());
            rankField.setText(item.getRank());
            priceField.setText(item.getPrice());
            volume_24hrField.setText(item.getVolumeHr24());
        }
    }
}

