FROM frolvlad/alpine-oraclejdk8

MAINTAINER Stuart Davidson <github@spedge.com>

# Add the configuration file for how the system might be laid out
ADD etc/docker/docker.yml .

# Copy the latest hangar jar. Be sure to have run mvn package first.
ADD target/hangar.jar ./hangar.jar

# Need somewhere to keep the data
VOLUME /data

# Expose the port also
EXPOSE 8080

# Kick it off
ENTRYPOINT ["java", "-jar", "./hangar.jar", "server", "./docker.yml"]