FROM rockylinux:9.2.20230513 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# install nodejs
RUN dnf install -y https://rpm.nodesource.com/pub_20.x/el/9/x86_64/nodesource-release-el9-1.noarch.rpm
RUN dnf install -y nodejs

# Install java
RUN dnf install -y java-21-openjdk-devel
ENV JAVA_HOME /usr/lib/jvm/java-21-openjdk

# run test
FROM first_docker as second_docker
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
COPY . /all_code
WORKDIR /all_code/capacitor-cypress
RUN git add ..
RUN git reset --hard
RUN rm -rf ../capacitor/node_modules
RUN rm -rf ./node_modules
RUN npm test
WORKDIR /all_code/capacitor
WORKDIR /all_code
RUN rm -rf ./capacitor-cypress/node_modules
RUN rm -rf ./capacitor/node_modules

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code/capacitor /capacitor
WORKDIR /capacitor

# start server
ENTRYPOINT ["/bin/bash"]
