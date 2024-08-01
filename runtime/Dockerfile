FROM icr.io/appcafe/open-liberty:kernel-slim-java21-openj9-ubi-minimal

ENV SEC_TLS_TRUSTDEFAULTCERTS true

# Add a Liberty server configuration including all necessary features
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/
COPY --chown=1001:0 src/main/liberty/config/configDropins/defaults/* /config/configDropins/defaults/

# Add PostgreSQL driver
COPY --chown=1001:0 target/lib/*postgresql* /opt/ol/wlp/usr/servers/defaultServer/jdbc/

# Adds the requested XML snippets to enable Liberty features and grow the image to be fit-for-purpose.
RUN features.sh

# Add the application itself
COPY --chown=1001:0 target/traqqr.ear /config/apps

# Adds the requested server configuration and populates caches to optimize the runtime.
RUN configure.sh