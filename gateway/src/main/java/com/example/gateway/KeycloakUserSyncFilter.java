package com.example.gateway;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.example.gateway.user.RegisterRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import com.example.gateway.user.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");

        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null){
            userId = registerRequest.getKeycloakId();
        }
        if (userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            //Register the user

                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            } else {
                                return Mono.empty();
                            }
                        } else {
                            log.info("User  already exist");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedrequest = exchange.getRequest().mutate()
                                .header("X-USER-ID", finalUserId)
                                .build();


                        return chain.filter(exchange.mutate().request(mutatedrequest).build());
                    }));
        }


        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {

        try{
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(jwtClaimsSet.getStringClaim("email"));
            registerRequest.setKeycloakId(jwtClaimsSet.getStringClaim("sub"));
            registerRequest.setPassword("dummy@12323");
            registerRequest.setFirstName(jwtClaimsSet.getStringClaim("given_name"));
            registerRequest.setLastName(jwtClaimsSet.getStringClaim("family_name"));

            return registerRequest;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
