package com.hackathon.bankingapp.dto.useractions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnableAutoInvestRequest {

    private String pin;

}