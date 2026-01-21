package com.CryptoProject.CryptoInfosys.service;

import com.CryptoProject.CryptoInfosys.dto.PricingDTO;
import com.CryptoProject.CryptoInfosys.model.PriceSnapshot;
import com.CryptoProject.CryptoInfosys.repository.PriceSnapshotRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingService {

    private final PriceSnapshotRepository priceRepo;

    public PricingService(PriceSnapshotRepository priceRepo) {
        this.priceRepo = priceRepo;
    }

    /**
     * Return latest prices from DB instead of CoinGecko
     * This avoids API rate limits completely
     */


    public List<PricingDTO> getPrices() {

        // TEMP circulating supply (can move to DB later)
        Map<String, Double> circulatingSupply = new HashMap<>();
        circulatingSupply.put("BTC", 19_700_000.0);
        circulatingSupply.put("ETH", 120_000_000.0);
        circulatingSupply.put("SOL", 440_000_000.0);
        circulatingSupply.put("USDT", 83_000_000_000.0);
        circulatingSupply.put("USDC", 33_000_000_000.0);
        circulatingSupply.put("BNB", 153_000_000.0);
        circulatingSupply.put("XRP", 54_000_000_000.0);
        circulatingSupply.put("TRX", 88_000_000_000.0);
        circulatingSupply.put("ADA", 35_000_000_000.0);

        List<PriceSnapshot> latestSnapshots = priceRepo.findLatestPrices();
        List<PricingDTO> prices = new ArrayList<>();

        for (PriceSnapshot latest : latestSnapshots) {

            PricingDTO dto = new PricingDTO();

            String asset = latest.getAssetSymbol();
            double latestPrice = latest.getPrice();

            dto.id = asset;
            dto.symbol = asset;
            dto.name = asset;
            dto.priceUsd = latestPrice;

            /* ---------------- SPARKLINE ---------------- */
            List<Double> history =
                    priceRepo.findRecentPrices(
                            asset,
                            PageRequest.of(0, 30)
                    );
            Collections.reverse(history);
            dto.sparkline = history;

            /* ---------------- 24H CHANGE ---------------- */
            LocalDateTime time24hAgo = LocalDateTime.now().minusHours(24);

            List<PriceSnapshot> oldSnapshots =
                    priceRepo.findPriceBefore(
                            asset,
                            time24hAgo,
                            PageRequest.of(0, 1)
                    );

            if (!oldSnapshots.isEmpty()) {
                double oldPrice = oldSnapshots.get(0).getPrice();
                dto.change24h =
                        ((latestPrice - oldPrice) / oldPrice) * 100;
            } else {
                dto.change24h = 0;
            }

            /* ---------------- MARKET CAP ---------------- */
            Double supply = circulatingSupply.get(asset);
            dto.marketCap =
                    supply != null ? supply * latestPrice : 0;

            prices.add(dto);
        }

        return prices;
    }


}
