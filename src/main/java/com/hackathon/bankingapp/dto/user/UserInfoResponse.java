package com.hackathon.bankingapp.dto.user;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {

    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private UUID accountNumber;
    private String hashedPassword;
}
