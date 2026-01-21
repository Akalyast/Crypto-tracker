package com.CryptoProject.CryptoInfosys.dto;

import java.util.List;

public class TaxSummaryDTO {
    public double totalRealizedGains;
    public double totalEstimatedTax;
    public double shortTermGains;
    public double longTermGains;
    public double shortTermTax;
    public double longTermTax;
    public List<TaxHintDTO> hints;
    public List<String> recommendations;
}
