package com.demo.annotation;

import com.demo.models.Permission;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Singleton
@InterceptorBean(HasPermission.class)
@AllArgsConstructor
public class PermissionInterceptor implements MethodInterceptor<Object, Object> {

    private final SecurityService securityService;

    @Nullable
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        Set<Permission> permissionsRequired = new HashSet();

        if (context.getExecutableMethod().isAnnotationPresent(HasPermission.class)) {
            permissionsRequired.addAll(
                    List.of(context.getExecutableMethod().getAnnotation(HasPermission.class).enumValues("value", Permission.class))
            );
        }

        //MutableArgumentValue authArgument = context.getParameters().values().stream().filter(p->p.getType().equals(Authentication.class)).findFirst().orElseThrow();
        //Authentication auth = (Authentication) authArgument.getValue();
        Authentication auth = securityService.getAuthentication()
                .orElseThrow(() -> new AuthorizationException(null));
        String subUser = Objects.toString(auth.getAttributes().get("sub"), null);
        String issUser = Objects.toString(auth.getAttributes().get("iss"), null);
        if(StringUtils.isEmpty(subUser) || StringUtils.isEmpty(issUser)){
            log.error("sub or iss is empty in the authentication attributes");
            throw new AuthorizationException(null);
        }
        //TODO.. find user by sub and iss and get permissions from db ->//getPermessi dall'utente autenticato e fai il confronto con quelli richiesti
        //chiamerò il service

        return context.proceed();
    }
}