package com.CryptoProject.CryptoInfosys.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.CryptoProject.CryptoInfosys.dto.TaxHintDTO;
import com.CryptoProject.CryptoInfosys.dto.TaxSummaryDTO;
import com.CryptoProject.CryptoInfosys.model.Trade;
import com.CryptoProject.CryptoInfosys.repository.TradeRepository;

@Service
public class TaxService {

    private final TradeRepository tradeRepo;

    // Tax rates (Indian tax rates - can be configured)
    private static final double SHORT_TERM_TAX_RATE = 0.30; // 30% for short-term (< 1 year)
    private static final double LONG_TERM_TAX_RATE = 0.20; // 20% for long-term (>= 1 year)
    private static final int LONG_TERM_DAYS = 365; // 1 year threshold

    public TaxService(TradeRepository tradeRepo) {
        this.tradeRepo = tradeRepo;
    }

    public TaxSummaryDTO calculateTaxHints(Long userId) {
        List<Trade> trades = tradeRepo.findByUserIdOrderByExecutedAtAsc(userId);
        
        TaxSummaryDTO summary = new TaxSummaryDTO();
        summary.hints = new ArrayList<>();
        summary.recommendations = new ArrayList<>();

        // Track buy trades (FIFO queue per asset)
        Map<String, Queue<BuyTrade>> buyQueue = new HashMap<>();
        double totalShortTermGains = 0;
        double totalLongTermGains = 0;
        double totalShortTermTax = 0;
        double totalLongTermTax = 0;

        // Process trades chronologically
        for (Trade trade : trades) {
            String asset = trade.getAssetSymbol();
            
            if (trade.getSide().name().equals("BUY")) {
                // Add to buy queue
                buyQueue.putIfAbsent(asset, new ArrayDeque<>());
                BuyTrade buyTrade = new BuyTrade(
                    trade.getPrice().doubleValue(),
                    trade.getQuantity().doubleValue(),
                    trade.getExecutedAt()
                );
                buyQueue.get(asset).offer(buyTrade);
            } 
            else if (trade.getSide().name().equals("SELL")) {
                // Process sell using FIFO
                Queue<BuyTrade> buys = buyQueue.get(asset);
                if (buys == null || buys.isEmpty()) continue;

                double remainingQty = trade.getQuantity().doubleValue();
                double sellPrice = trade.getPrice().doubleValue();
                LocalDateTime sellDate = trade.getExecutedAt();

                List<TaxHintDTO> tradeHints = new ArrayList<>();

                while (remainingQty > 0 && !buys.isEmpty()) {
                    BuyTrade buy = buys.peek();
                    double qtyToUse = Math.min(remainingQty, buy.quantity);

                    // Calculate holding period
                    long daysHeld = ChronoUnit.DAYS.between(buy.buyDate, sellDate);
                    boolean isLongTerm = daysHeld >= LONG_TERM_DAYS;

                    // Calculate gain
                    double gain = (sellPrice - buy.price) * qtyToUse;

                    // Calculate tax
                    double taxRate = isLongTerm ? LONG_TERM_TAX_RATE : SHORT_TERM_TAX_RATE;
                    double tax = gain > 0 ? gain * taxRate : 0;

                    // Update totals
                    if (isLongTerm) {
                        totalLongTermGains += gain;
                        totalLongTermTax += tax;
                    } else {
                        totalShortTermGains += gain;
                        totalShortTermTax += tax;
                    }

                    // Create tax hint for this trade
                    if (gain != 0) {
                        TaxHintDTO hint = new TaxHintDTO();
                        hint.asset = asset;
                        hint.symbol = asset;
                        hint.realizedGain = gain;
                        hint.estimatedTax = tax;
                        hint.holdingPeriod = isLongTerm ? "LONG_TERM" : "SHORT_TERM";
                        hint.daysHeld = (int) daysHeld;
                        hint.hint = generateHint(gain, isLongTerm, daysHeld);
                        hint.hintType = determineHintType(gain, tax, isLongTerm, daysHeld);
                        tradeHints.add(hint);
                    }

                    // Update buy queue
                    buy.quantity -= qtyToUse;
                    remainingQty -= qtyToUse;
                    if (buy.quantity <= 0) {
                        buys.poll();
                    }
                }

                summary.hints.addAll(tradeHints);
            }
        }

        // Calculate totals
        summary.totalRealizedGains = totalShortTermGains + totalLongTermGains;
        summary.totalEstimatedTax = totalShortTermTax + totalLongTermTax;
        summary.shortTermGains = totalShortTermGains;
        summary.longTermGains = totalLongTermGains;
        summary.shortTermTax = totalShortTermTax;
        summary.longTermTax = totalLongTermTax;

        // Generate recommendations
        summary.recommendations = generateRecommendations(summary);

        return summary;
    }

    private String generateHint(double gain, boolean isLongTerm, long daysHeld) {
        if (gain <= 0) {
            return "No tax liability on losses. Losses can offset gains.";
        }

        if (isLongTerm) {
            return String.format("Long-term capital gain (held %d days). Tax rate: 20%%", daysHeld);
        } else {
            if (daysHeld < LONG_TERM_DAYS - 30) {
                return String.format("Short-term gain. Consider holding for %d more days to qualify for long-term tax benefits (20%% vs 30%%).", 
                    LONG_TERM_DAYS - daysHeld);
            }
            return String.format("Short-term capital gain (held %d days). Tax rate: 30%%", daysHeld);
        }
    }

    private String determineHintType(double gain, double tax, boolean isLongTerm, long daysHeld) {
        if (gain <= 0) {
            return "INFO";
        }
        
        if (!isLongTerm && daysHeld < LONG_TERM_DAYS - 30) {
            return "OPTIMIZATION";
        }
        
        if (tax > 100000) { // High tax amount
            return "WARNING";
        }
        
        return "INFO";
    }

    private List<String> generateRecommendations(TaxSummaryDTO summary) {
        List<String> recs = new ArrayList<>();

        if (summary.totalRealizedGains <= 0) {
            recs.add("You have no realized gains. No tax liability currently.");
            return recs;
        }

        if (summary.shortTermGains > summary.longTermGains) {
            recs.add("Consider holding assets longer (1+ years) to benefit from lower long-term capital gains tax (20% vs 30%).");
        }

        if (summary.totalEstimatedTax > 100000) {
            recs.add("High tax liability detected. Consider tax-loss harvesting by selling underperforming assets to offset gains.");
        }

        if (summary.shortTermTax > 0 && summary.longTermTax > 0) {
            recs.add("You have both short-term and long-term gains. Ensure proper documentation for tax filing.");
        }

        recs.add("Consult with a tax advisor for accurate tax planning and filing.");
        recs.add("Keep detailed records of all trades for tax purposes.");

        return recs;
    }

    // Helper class to track buy trades
    private static class BuyTrade {
        double price;
        double quantity;
        LocalDateTime buyDate;

        BuyTrade(double price, double quantity, LocalDateTime buyDate) {
            this.price = price;
            this.quantity = quantity;
            this.buyDate = buyDate;
        }
    }
}
