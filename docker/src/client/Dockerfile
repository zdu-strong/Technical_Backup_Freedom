FROM rockylinux:9.3.20231119 as first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en
ENV LANG en_US.UTF-8
ENV LC_ALL C.UTF-8

# install nodejs
RUN dnf module install -y nodejs:20

# install serve
RUN npm install --location=global serve

# run test
FROM first_docker as second_docker
RUN dnf install -y git
RUN dnf install -y https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.rpm
ENV JAVA_HOME /usr/lib/jvm/jdk-21-oracle-x64
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb gtk2-devel gtk3-devel libnotify-devel nss libXScrnSaver alsa-lib
COPY . /all_code
WORKDIR /all_code/springboot
RUN git add ..
RUN git reset --hard
RUN chmod +x mvn
WORKDIR /all_code/react-cypress
RUN rm -rf ../react/node_modules
RUN rm -rf ./node_modules
RUN npm test

# compile code
WORKDIR /all_code/react
RUN npm run build

# copy build folder
FROM first_docker
COPY --from=second_docker /all_code/react/build /build

EXPOSE 443

# start server
ENTRYPOINT ["serve", "--single", "--cors", "--no-clipboard", "--no-port-switching", "--no-request-logging", "--listen=443", "build"]
