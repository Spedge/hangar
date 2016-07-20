FROM alpine:3.3
MAINTAINER Stuart Davidson <stuart.davidson@skyscanner.net>

ENV \
    MAVEN_HOME="/usr/share/maven" \
    JAVA_HOME="/usr/lib/jvm/default-jvm" \
    JAVA_PREFS="/.java/.userPrefs" \
    BUILD_DEPS="curl openjdk8 bash"

ENV PATH=${JAVA_HOME}/bin:${PATH}

RUN \
    # Install Java8
    apk add --update ${BUILD_DEPS} \

    # Default DNS cache TTL is -1. DNS records, like, change, man
    && grep '^networkaddress.cache.ttl=' ${JAVA_HOME}/jre/lib/security/java.security || echo 'networkaddress.cache.ttl=60' >> ${JAVA_HOME}/jre/lib/security/java.security \

    # Cleanup
    && rm -rf -- /var/cache/apk/* \

    # Install Maven
    && MAVEN_VERSION=3.3.3 \
    && cd /usr/share \
    && wget -q http://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -O - | tar xzf - \
    && mv /usr/share/apache-maven-${MAVEN_VERSION} /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# To store Java preferences
RUN mkdir -p ${JAVA_PREFS}
RUN chown -R nobody.nobody ${JAVA_PREFS}

# To set-up test
RUN mkdir -p -m 777 /opt/test
RUN chmod 777 /opt/test
ADD include /opt/test/

USER nobody
WORKDIR /opt/test

ENTRYPOINT ["bash", "-ex", "/opt/test/test.sh"]