package com.bankingsystem.account_service.dto;

import com.bankingsystem.account_service.model.Currency;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {

    private Long accountId; // Unique ID of the account

    private Long userId; // ID of the user associated with the account

    private String accountNumber; // Unique account number

    private String accountName;
    private String accountType; // Type of account (e.g., savings, checking)

    private BigDecimal balance; // Current balance of the account

    private Currency currency; // Currency type (e.g., USD, BDT)

    private String status; // Status of the account (e.g., active, inactive, closed)

    private OffsetDateTime createdAt; // Timestamp when the account was created

    private OffsetDateTime updatedAt; // Timestamp when the account was last updated

    private OffsetDateTime deletedAt; // Timestamp when the account was deleted (if applicable)
}