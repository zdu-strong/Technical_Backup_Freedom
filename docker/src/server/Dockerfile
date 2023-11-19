FROM rockylinux:9.2.20230513 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# Install java
RUN dnf install -y https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.rpm
ENV JAVA_HOME /usr/lib/jvm/jdk-21-oracle-x64

# compile code
FROM first_docker as second_docker
RUN dnf install -y git
COPY . /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
RUN ./mvn clean package --define database.mysql.jdbc.url=host.docker.internal:3306
RUN mv target/*.jar target/server.jar

# copy jar
FROM first_docker
COPY --from=second_docker /all_code/springboot/target/server.jar /server.jar
ENV SERVER_SHUTDOWN GRACEFUL

# 8080 is server port, Since the server port is configured as 8080 in the client, the server port must be 8080 during runtime
EXPOSE 8080

# start server
ENTRYPOINT ["java", "-jar", "server.jar"]
