FROM centos:centos7

RUN yum update -y && \
    yum install -y sudo && \
    yum install -y java-1.8.0-openjdk-devel && \
    curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo && \
    sudo yum install -y sbt && \
    yum clean all && \
    mkdir -p /test

COPY src /test/src
COPY project /test/project
COPY build.sbt /test/
