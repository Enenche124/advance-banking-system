package com.apostle.dtos.requests;

import java.math.BigDecimal;

public record DepositRequest(
        Long receiverId,
        BigDecimal amount,
        String note
) { }
