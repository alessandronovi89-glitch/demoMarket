package com.demo.service;

import com.demo.configuration.SecurityConfiguration;
import com.demo.dto.AuthTokens;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@Singleton
@AllArgsConstructor
public class AuthService {

    private final SecurityConfiguration securityConfig;

    @Client()
    private final HttpClient httpClient;

    @SneakyThrows
    public URI login(State state) {
        AuthenticationRequest request = new AuthenticationRequest.Builder(
                ResponseType.CODE,
                new Scope("openid"),
                new ClientID(securityConfig.getClientId()),
                new URI(securityConfig.getRedirectUri()))
                .endpointURI(securityConfig.getProvider().getAuthorizationEndpointURI())
                .state(state)
                //.prompt(new Prompt("login")) -> forza sempre la schermata di login
                .build();
        return request.toURI();
        //todo valutare se fare PKCE
    }

    @SneakyThrows
    public AuthTokens exchangeCodeForToken(String code, @NotBlank String state, String stateCookie)  {
        if (!stateCookie.equals(state)) {
            throw new SecurityException("Invalid state");
        }
        ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(securityConfig.getClientId()),
                new Secret(securityConfig.getClientSecret()));
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(new AuthorizationCode(code),
                new URI(securityConfig.getRedirectUri()));
        TokenRequest request = new TokenRequest(securityConfig.getProvider().getTokenEndpointURI(), clientAuth,
                codeGrant, null);
        HTTPResponse response = request.toHTTPRequest().send();
        if(!response.indicatesSuccess()){
            log.error("Error exchanging code for token:: " + OIDCTokenResponseParser.parse(response).toErrorResponse().getErrorObject().getDescription());
            throw new RuntimeException("Error exchanging code for token: ");
        }
        AccessTokenResponse successResponse = OIDCTokenResponseParser.parse(response).toSuccessResponse();
        return AuthTokens.builder()
                .accessToken(successResponse.getTokens().getAccessToken().getValue())
                .refreshToken(successResponse.getTokens().getRefreshToken().getValue())
                .expiresIn(successResponse.getTokens().getAccessToken().getLifetime())
                .build();
    }

    @SneakyThrows
    public AuthTokens refreshToken(String refreshToken) {
        ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(securityConfig.getClientId()),
                new Secret(securityConfig.getClientSecret()));
        RefreshTokenGrant refreshGrant = new RefreshTokenGrant(new RefreshToken(refreshToken));

        TokenRequest request = new TokenRequest(securityConfig.getProvider().getTokenEndpointURI(), clientAuth,
                refreshGrant, null);
        HTTPResponse response = request.toHTTPRequest().send();
        if(!response.indicatesSuccess()){
            log.error("Error refreshing token: " + OIDCTokenResponseParser.parse(response).toErrorResponse().getErrorObject().getDescription());
            throw new RuntimeException("Error refreshing token");
        }
        AccessTokenResponse successResponse = OIDCTokenResponseParser.parse(response).toSuccessResponse();
        return AuthTokens.builder()
                .accessToken(successResponse.getTokens().getAccessToken().getValue())
                .refreshToken(successResponse.getTokens().getRefreshToken().getValue())
                .expiresIn(successResponse.getTokens().getAccessToken().getLifetime())
                .build();
    }


}
