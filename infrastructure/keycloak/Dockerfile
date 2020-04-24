FROM jboss/keycloak


ADD ./realm-export-prod.json /opt/jboss/keycloak/imports/realm-export-prod.json
ADD themes /opt/jboss/keycloak/themes

ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin
ENV JGROUPS_TRANSPORT_STACK=tcp
ENV JGROUPS_DISCOVERY_PROTOCOL=dns.DNS_PING
EXPOSE 8080




CMD ["-b","0.0.0.0","-Dkeycloak.import=/opt/jboss/keycloak/imports/realm-export.json","-Dkeycloak.profile.feature.docker=enabled"]
