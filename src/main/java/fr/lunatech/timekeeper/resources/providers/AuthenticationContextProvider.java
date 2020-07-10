package fr.lunatech.timekeeper.resources.providers;

import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.services.AuthenticationContext;
import fr.lunatech.timekeeper.services.UserService;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.requests.AuthenticationRequest;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.SecurityIdentity;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuthenticationContextProvider {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationContextProvider.class);

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity identity;

    public AuthenticationContext context() {
        if (identity.getPrincipal() instanceof io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) {
            final var authenticationRequest = getAuthenticationRequest((OidcJwtCallerPrincipal) identity.getPrincipal());

            try {
                return userService.authenticate(authenticationRequest);
            } catch (IllegalEntityStateException e) {
                throw new ForbiddenException(e.getMessage(), e);
            }

        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Unknown identity.getPrincipal: %s", identity.getPrincipal()));
            }
            throw new UnauthorizedException();
        }
    }

    private AuthenticationRequest getAuthenticationRequest(OidcJwtCallerPrincipal jwtCallerPrincipal) {
        final var jwtClaims = jwtCallerPrincipal.getClaims();
        final String email = jwtClaims.getClaimValueAsString("email");
        final String firstName = jwtClaims.getClaimValueAsString("given_name");
        final String lastName = jwtClaims.getClaimValueAsString("family_name");
        final String organization = jwtClaims.getClaimValueAsString("organization");
        final String picture = jwtClaims.getClaimValueAsString("picture");
        final List<Profile> profiles = getProfiles(jwtCallerPrincipal, jwtClaims);

        return new AuthenticationRequest(firstName, lastName, email, picture, profiles, organization);
    }

    private List<Profile> getProfiles(OidcJwtCallerPrincipal jwtCallerPrincipal, JwtClaims jwtClaims) {
        JsonObject realmAccess = null;
        try {
            realmAccess = jwtClaims.getClaimValue("realm_access", JsonObject.class);
        } catch (MalformedClaimException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Can't retrieve a valid [realm_access] jwt claims: %s", jwtCallerPrincipal));
            }
            throw new UnauthorizedException();
        }

        final List<Profile> profiles = Optional.ofNullable(realmAccess)
                .map(jsonObject -> jsonObject
                        .getJsonArray("roles")
                        .stream()
                        .map(Object::toString)
                        .map(s -> s.replaceAll("\"", ""))
                        .map(Profile::getByName)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
                )
                .orElseGet(Collections::emptyList);

        if (profiles.isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("No profile detected: %s", jwtCallerPrincipal));
            }
            throw new UnauthorizedException();
        }
        return profiles;
    }
}
