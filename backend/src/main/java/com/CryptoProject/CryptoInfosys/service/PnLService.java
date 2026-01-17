package com.CryptoProject.CryptoInfosys.service;

import com.CryptoProject.CryptoInfosys.dto.PnLDTO;
import com.CryptoProject.CryptoInfosys.dto.PnLSummaryDTO;
import com.CryptoProject.CryptoInfosys.dto.PnLTimelineDTO;
import com.CryptoProject.CryptoInfosys.model.Trade;
import com.CryptoProject.CryptoInfosys.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PnLService {

    private final TradeRepository tradeRepo;
    private final PricingService pricingService;
    private final NotificationService notificationService;


    public PnLService(
            TradeRepository tradeRepo,
            PricingService pricingService,
            NotificationService notificationService
    ) {
        this.tradeRepo = tradeRepo;
        this.pricingService = pricingService;
        this.notificationService = notificationService;
    }

    // =========================================================
    // 🔵 EXISTING WORKING METHODS (DO NOT TOUCH)
    // =========================================================

    public PnLSummaryDTO calculatePnL(Long userId) {

        List<PnLDTO> assetPnL = calculateAssetPnL(userId);

        PnLSummaryDTO summary = new PnLSummaryDTO();
        summary.assets = assetPnL;

        summary.totalRealizedPnL =
                assetPnL.stream().mapToDouble(p -> p.realizedPnL).sum();

        summary.totalUnrealizedPnL =
                assetPnL.stream().mapToDouble(p -> p.unrealizedPnL).sum();

        return summary;
    }

    private List<PnLDTO> calculateAssetPnL(Long userId) {

        List<Trade> trades = tradeRepo.findByUserId(userId);
        Map<String, PnLDTO> pnlMap = new HashMap<>();

        Map<String, Double> currentPrices = new HashMap<>();
        pricingService.getPrices().forEach(p ->
                currentPrices.put(p.symbol, p.priceUsd)
        );

        for (Trade t : trades) {

            String asset = t.getAssetSymbol();
            pnlMap.putIfAbsent(asset, new PnLDTO());
            PnLDTO dto = pnlMap.get(asset);

            dto.asset = asset;
            dto.symbol = asset;

            double qty = t.getQuantity().doubleValue();
            double price = t.getPrice().doubleValue();

            if (t.getSide().name().equals("BUY")) {
                double totalCost =
                        (dto.avgBuyPrice * dto.quantity)
                        + (price * qty);

                dto.quantity += qty;
                dto.avgBuyPrice =
                        dto.quantity == 0 ? 0 : totalCost / dto.quantity;
            }

            if (t.getSide().name().equals("SELL")) {
                dto.realizedPnL +=
                        (price - dto.avgBuyPrice) * qty;

                dto.quantity -= qty;
            }
        }

        for (PnLDTO dto : pnlMap.values()) {
            dto.currentPrice =
                    currentPrices.getOrDefault(dto.symbol, 0.0);

            dto.unrealizedPnL =
                    (dto.currentPrice - dto.avgBuyPrice)
                    * dto.quantity;
        }

        return new ArrayList<>(pnlMap.values());
    }

    public List<PnLTimelineDTO> calculatePnLTimeline(Long userId) {

        List<Trade> trades =
                tradeRepo.findByUserIdOrderByExecutedAtAsc(userId);

        Map<String, Double> avgBuyPriceMap = new HashMap<>();
        Map<String, Double> quantityMap = new HashMap<>();

        TreeMap<LocalDate, Double> dailyPnL = new TreeMap<>();
        double cumulativePnL = 0;

        for (Trade t : trades) {

            String asset = t.getAssetSymbol();

            if (t.getSide().name().equals("BUY")) {

                double oldQty = quantityMap.getOrDefault(asset, 0.0);
                double oldAvg = avgBuyPriceMap.getOrDefault(asset, 0.0);

                double tradeQty = t.getQuantity().doubleValue();
                double tradePrice = t.getPrice().doubleValue();

                double newQty = oldQty + tradeQty;
                double newAvg =
                        ((oldAvg * oldQty)
                        + (tradePrice * tradeQty))
                        / newQty;

                quantityMap.put(asset, newQty);
                avgBuyPriceMap.put(asset, newAvg);
            }

            if (t.getSide().name().equals("SELL")) {

                double avgBuy = avgBuyPriceMap.getOrDefault(asset, 0.0);

                double pnl =
                        (t.getPrice().doubleValue() - avgBuy)
                        * t.getQuantity().doubleValue();

                LocalDate date = t.getExecutedAt().toLocalDate();

                dailyPnL.put(
                        date,
                        dailyPnL.getOrDefault(date, 0.0) + pnl
                );

                quantityMap.put(
                        asset,
                        quantityMap.get(asset)
                        - t.getQuantity().doubleValue()
                );
            }
        }

        List<PnLTimelineDTO> timeline = new ArrayList<>();

        for (LocalDate date : dailyPnL.keySet()) {
            cumulativePnL += dailyPnL.get(date);

            PnLTimelineDTO dto = new PnLTimelineDTO();
            dto.date = date;
            dto.cumulativePnL = cumulativePnL;

            timeline.add(dto);
        }

        return timeline;
    }

    // =========================================================
    // 🔥 NEW DASHBOARD HELPER METHODS (SAFE)
    // =========================================================

    public double getTotalRealizedPnL(Long userId) {
        return calculatePnL(userId).totalRealizedPnL;
    }

    public double getTotalUnrealizedPnL(Long userId) {
        return calculatePnL(userId).totalUnrealizedPnL;
    }
    public double getTotalRealizedPnL(String email) {
    	Long userId = getUserIdByEmail(email);
        double realized = getTotalRealizedPnL(userId);

        // 🎉 PROFIT ALERT (only once)
        if (realized >= 10000) {

            boolean alreadySent =
                    notificationService.hasNotification(
                            email,
                            "Profit Alert",
                            "INFO"
                    );

            if (!alreadySent) {
                notificationService.createNotification(
                        email,
                        "Profit Alert",
                        "Congratulations! You earned ₹" + realized,
                        "INFO"
                );
            }
        }

        return realized;
    }

    public double getTotalUnrealizedPnL(String email) {
    	Long userId = getUserIdByEmail(email);
        double unrealized = getTotalUnrealizedPnL(userId);

        // 🚨 LOSS ALERT (only once)
        if (unrealized <= -5000) {

            boolean alreadySent =
                    notificationService.hasNotification(
                            email,
                            "Loss Alert",
                            "ALERT"
                    );

            if (!alreadySent) {
                notificationService.createNotification(
                        email,
                        "Loss Alert",
                        "Your portfolio is down ₹" + Math.abs(unrealized),
                        "ALERT"
                );
            }
        }

        return unrealized;
    }
    private Long getUserIdByEmail(String email) {
        return tradeRepo.findUserIdByEmail(email);
    }
    
}
