package com.example.tokengenerator.api;

import com.example.tokengenerator.service.JwtService;
import com.example.tokengenerator.service.TokenForwarder;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenForwarder tokenForwarder;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> issueToken(@Valid @RequestBody AuthRequest request) {
        String token = jwtService.generateToken(request.getUsername());
        try {
            tokenForwarder.forward(token);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to forward JWT", ex);
        }
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
