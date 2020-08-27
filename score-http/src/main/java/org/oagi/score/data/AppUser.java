package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AppUser {

    private BigInteger appUserId;
    private String loginId;
    private String password;
    private String name;
    private String organization;
    private boolean developer;

}