package com.cashrich.coinrich.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashrich.coinrich.CoinsAdapter;
import com.cashrich.coinrich.LoginActivity;
import com.cashrich.coinrich.R;
import com.cashrich.coinrich.databinding.FragmentHomeBinding;
import com.cashrich.coinrich.utils.OkHttpUtil;
import com.cashrich.coinrich.utils.Utility;
import com.cashrich.coinrich.vo.MarketDataResponseVo;
import com.cashrich.coinrich.vo.ResponseVo;
import com.cashrich.coinrich.vo.SessionVo;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            JSONObject jsonObject = createCoinsJsonPayload();
            String url = getResources().getString(R.string.baseUrl).concat(getResources().getString(R.string.getMarketData));
            Map<String, String> headers = Utility.getMarketApiHeaders(((SessionVo) requireContext().getApplicationContext()).getSessionId());
            ResponseVo responseVo = OkHttpUtil.cashRichPostRequest(jsonObject, url, headers);
            requireActivity().runOnUiThread(() -> {
                if (responseVo.getVal() == 1) {
                    List<MarketDataResponseVo> coinsList = getCoinsFromJsonResponse(responseVo);
                    if (null == coinsList) {
                        Snackbar.make(requireView(), "Error Occurred", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Snackbar.make(requireView(), "Welcome to CoinRich!", Snackbar.LENGTH_SHORT).show();
                    createViewToDisplayCoins(coinsList, binding.recyclerView);
                } else {
                    Snackbar.make(requireView(), responseVo.getResponse().concat(" Please Login Again."), Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        requireActivity().finish();
                    }, 3000);
                }
            });
        });
        return root;
    }

    private static JSONObject createCoinsJsonPayload() {
        JSONObject object = new JSONObject();
        try {
            List<String> coinList = Arrays.asList("BTC", "LTC", "ETH", "XRP", "DOGE", "ADA");
            JSONArray coins = new JSONArray(coinList);
            object.put("coins", coins);
            return object;
        } catch (JSONException e) {
            Log.e("StringToJson", "Error occurred while converting field values to Json");
            throw new RuntimeException("Error Occurred");
        }
    }

    private static List<MarketDataResponseVo> getCoinsFromJsonResponse(ResponseVo responseVo) {
        try {
            JSONArray coinsArray = new JSONArray(responseVo.getResponse());
            List<MarketDataResponseVo> coins = new ArrayList<>();
            for (int i = 0; i < coinsArray.length(); i++) {
                JSONObject coinJson = coinsArray.optJSONObject(i);
                MarketDataResponseVo coin = new MarketDataResponseVo();
                coin.setRank(coinJson.optString("rank"));
                coin.setSymbol(coinJson.optString("symbol"));
                coin.setPrice(coinJson.optString("price"));
                coin.setVolumeHr24(coinJson.optString("volume_24h"));
                coins.add(coin);
            }
            return coins;
        } catch (JSONException e) {
            Log.e("StringToJson", "Error occurred while converting response to Json object");
            return null;
        }
    }

    private static void createViewToDisplayCoins(List<MarketDataResponseVo> coinsList, RecyclerView recyclerView) {

// Create a custom adapter for your RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        CoinsAdapter adapter = new CoinsAdapter(coinsList);
        recyclerView.setAdapter(adapter);
//// Set up your RecyclerView in your fragment or activity layout file, and assign it an ID
//
//// Find the RecyclerView by its ID
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//// Set the adapter on the RecyclerView
//        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}