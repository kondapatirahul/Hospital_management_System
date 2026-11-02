package com.healthcare.hospital.modules.workflows.dto;
import java.util.Map;
public record LaboratoryBoardDto(long totalSamples, long pendingReceipt, long awaitingProcessing,
                                 long agingOver24Hours, long rejected, Map<String,Long> sampleTypes) {
    public LaboratoryBoardDto { sampleTypes = Map.copyOf(sampleTypes); }
}
