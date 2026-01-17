package com.CryptoProject.CryptoInfosys.service;

import com.CryptoProject.CryptoInfosys.dto.PricingDTO;
import com.CryptoProject.CryptoInfosys.dto.RiskAlertDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskAnalysisService {

    private final NotificationService notificationService;

    public RiskAnalysisService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // =========================================================
    // 🔵 EXISTING METHOD (DO NOT TOUCH – FRONTEND DEPENDS ON THIS)
    // =========================================================
    public List<RiskAlertDTO> analyze(List<PricingDTO> prices) {

        List<RiskAlertDTO> alerts = new ArrayList<>();

        for (PricingDTO p : prices) {

            RiskAlertDTO alert = new RiskAlertDTO();
            alert.asset = p.name;
            alert.symbol = p.symbol;

            // 🔴 HIGH RISK
            if (Math.abs(p.change24h) > 15) {
                alert.riskLevel = "HIGH";
                alert.reason = "Extreme 24h price volatility";
            }
            // 🟡 MEDIUM RISK
            else if (p.marketCap < 10_000_000_000L) {
                alert.riskLevel = "MEDIUM";
                alert.reason = "Low market capitalization";
            }
            // 🟢 LOW RISK
            else {
                alert.riskLevel = "LOW";
                alert.reason = "Stable market conditions";
            }

            alerts.add(alert);
        }

        return alerts;
    }

    // =========================================================
    // 🔥 NEW METHOD (NOTIFICATION-AWARE – SAFE ADDITION)
    // =========================================================
    public List<RiskAlertDTO> analyzeWithNotifications(
            List<PricingDTO> prices,
            String email
    ) {

        List<RiskAlertDTO> alerts = analyze(prices); // reuse existing logic

        for (RiskAlertDTO alert : alerts) {

            // 🔔 Trigger notification ONLY for HIGH risk
            if ("HIGH".equals(alert.riskLevel)) {

                boolean alreadySent =
                        notificationService.hasNotification(
                                email,
                                "High Risk Alert",
                                "WARNING"
                        );

                if (!alreadySent) {
                    notificationService.createNotification(
                            email,
                            "High Risk Alert",
                            alert.symbol +
                            " is showing extreme volatility. Trade carefully.",
                            "WARNING"
                    );
                }
            }
        }

        return alerts;
    }
}
