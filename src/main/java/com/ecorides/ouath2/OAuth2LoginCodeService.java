package com.ecorides.ouath2;

import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuth2LoginCodeService {

    private final Map<String, OAuth2LoginCode> codes = new ConcurrentHashMap<>();

    public String createCode(User user) {

        String code = UUID.randomUUID()
                .toString();

        OAuth2LoginCode loginCode = new OAuth2LoginCode(user.getId(), user.getEmail(), user.getUserRole(), Instant.now()
                .plusSeconds(60));

        codes.put(code, loginCode);

        System.out.println("OAUTH CODE CREATED: " + code);

        return code;
    }

    public OAuth2LoginCode consumeCode(String code) {

        System.out.println("OAUTH CODE RECEIVED: " + code);
        System.out.println("STORED OAUTH CODES: " + codes.keySet());

        OAuth2LoginCode loginCode = codes.remove(code);

        if (loginCode == null) {
            throw new BadRequestException("Invalid or expired OAuth login code");
        }

        if (loginCode.expiresAt()
                .isBefore(Instant.now())) {
            throw new BadRequestException("OAuth login code expired");
        }

        return loginCode;
    }
}