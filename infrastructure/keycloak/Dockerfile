FROM jboss/keycloak


ARG environment=dev

RUN echo ${environment}

ADD realm-export-${environment}.json /opt/jboss/keycloak/imports/realm-export.json
ADD themes /opt/jboss/keycloak/themes

ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin
ENV JGROUPS_TRANSPORT_STACK=tcp
ENV JGROUPS_DISCOVERY_PROTOCOL=dns.DNS_PING
ENV JGROUPS_DISCOVERY_PROPERTIES=dns_query=some.dummy.host.local

EXPOSE 8080


CMD ["-b","0.0.0.0","-Dkeycloak.import=/opt/jboss/keycloak/imports/realm-export.json","-Dkeycloak.profile.feature.docker=enabled"]
