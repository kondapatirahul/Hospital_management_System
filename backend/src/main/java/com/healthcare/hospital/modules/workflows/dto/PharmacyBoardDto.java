package com.healthcare.hospital.modules.workflows.dto;
import java.math.BigDecimal;
import java.util.Map;
public record PharmacyBoardDto(long batchCount, long units, BigDecimal inventoryValue, long expired,
                               long expiringWithin30Days, long lowStock, Map<String,Long> statuses) {
    public PharmacyBoardDto { statuses = Map.copyOf(statuses); }
}
