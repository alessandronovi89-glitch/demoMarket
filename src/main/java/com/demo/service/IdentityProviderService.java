package com.demo.service;

import com.demo.dto.keycloak.EmailRequest;
import com.demo.dto.keycloak.ResetPasswordRequest;
import com.demo.dto.keycloak.User;
import com.demo.dto.keycloak.UserSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IdentityProviderService {

    CompletionStage<List<User>> getUsers(String token, UserSearchCriteria criteria);

    CompletionStage<Optional<User>> getUser(String token, String id);

    CompletionStage<String> createUser(String token, User user);

    CompletionStage<Void> deleteUser(String token, String id);

    CompletionStage<Void> resetPassword(String token, ResetPasswordRequest request);

    CompletionStage<Void> sendVerifyEmail(String token, EmailRequest request);

    CompletionStage<Void> assignRealmRole(String token, String userId, String roleName);
}
