package com.demo.service;

import com.demo.configuration.SecurityConfiguration;
import com.demo.dto.AuthTokens;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
@AllArgsConstructor
public class AuthService {

    private final SecurityConfiguration securityConfig;

    @Client()
    private final HttpClient httpClient;

    //TODO login

    @SneakyThrows //WIP
    public CompletionStage<AuthTokens> exchangeCodeForToken(String code)  {

        //WIP----------------------------------------------------
        Issuer issuer = new Issuer(securityConfig.getIssuerUrl());
        OIDCProviderMetadata provider =
                OIDCProviderMetadata.resolve(
                        issuer
                );
        provider.getTokenEndpointURI();
        provider.getGrantTypes();
        ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(securityConfig.getClientId()),
                new Secret(securityConfig.getClientSecret()));
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(new AuthorizationCode(code),
                new URI(securityConfig.getRedirectUri()));
        TokenRequest request = new TokenRequest(provider.getTokenEndpointURI(), clientAuth,
                codeGrant, null); //scope sarebbe openid già messo prima (non serve..)
        HTTPRequest toHTTPRequest = request.toHTTPRequest();
        TokenResponse tokenResponse= OIDCTokenResponseParser.parse(toHTTPRequest.send());
        //HA I TOKEN!.. perfect!
        OIDCTokenResponse successResponse = (OIDCTokenResponse) tokenResponse.toSuccessResponse();
        JWT idToken = successResponse.getOIDCTokens().getIDToken();
        AccessToken accessToken = successResponse.getOIDCTokens().getAccessToken();
        RefreshToken refreshToken = successResponse.getOIDCTokens().getRefreshToken();
        //----------------------------------------------------


        //The state should match the state created when the /login service was called. ?? (da capire lo state)
        String body = "grant_type=" + securityConfig.getGrantType()
                + "&code=" + code
                + "&client_id=" + securityConfig.getClientId()
                + "&client_secret=" + securityConfig.getClientSecret()
                + "&redirect_uri=" + securityConfig.getRedirectUri();

        log.info("Exchanging code for token at {}", securityConfig.getTokenUrl());
        return httpClient.toAsync().exchange(
                    HttpRequest.POST(securityConfig.getTokenUrl(), body)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE), AuthTokens.class)
                    .thenApply(result-> {
                        log.debug("get token: " + result);
                        return result.getBody().orElse(null);
                    })
                    .exceptionally((ex)->{
                        log.error("Error get token", ex);
                        throw new CompletionException(ex);
                    });
    }

    public CompletionStage<AuthTokens> refreshToken(String refreshToken) {
        String body = "grant_type=refresh_token"
                + "&refresh_token=" + refreshToken
                + "&client_id=" + securityConfig.getClientId()
                + "&client_secret=" + securityConfig.getClientSecret();

        log.info("refreshing token at {}", securityConfig.getTokenUrl());
        return httpClient.toAsync().exchange(
                        HttpRequest.POST(securityConfig.getTokenUrl(), body)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE), AuthTokens.class)
                .thenApply(result-> {
                    log.debug("response refresh: " + result);
                    return result.getBody().orElse(null);
                })
                .exceptionally((ex)->{
                    log.error("Error refreshing token", ex);
                    throw new CompletionException(ex);
                });
    }
}
