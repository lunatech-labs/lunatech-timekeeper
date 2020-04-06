package fr.lunatech.timekeeper.model;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object is created from the JWT token sent by Quarkus.
 * It used from UserResource.
 * @author Nicolas Martignole
 */
public class User {
    private String name;
    private String givenName;
    private String familyName;
    private String email;

    private static Logger logger = LoggerFactory.getLogger(User.class);


    public User(SecurityIdentity identity) {

        logger.debug("create user from "+identity.getPrincipal());

        setName(identity.getPrincipal().getName());

       logger.debug(identity.getPrincipal().getClass().toString());

        if(identity.getPrincipal() instanceof io.quarkus.oidc.runtime.OidcJwtCallerPrincipal){
            OidcJwtCallerPrincipal jwtCallerPrincipal = (io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) identity.getPrincipal();

            // logger.debug("Claims" + jwtCallerPrincipal.getClaims());
            setEmail(jwtCallerPrincipal.getClaims().getClaimValueAsString("email"));
            setGivenName(jwtCallerPrincipal.getClaims().getClaimValueAsString("given_name"));
            setFamilyName(jwtCallerPrincipal.getClaims().getClaimValueAsString("family_name"));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
