package com.cashrich.coinrich.vo;

public class MarketDataResponseVo {

    private String rank;
    private String symbol;
    private String price;
    private String volumeHr24;
    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String string) {
        this.symbol = string;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getVolumeHr24() {
        return volumeHr24;
    }
    public void setVolumeHr24(String volumeHr24) {
        this.volumeHr24 = volumeHr24;
    }
}
