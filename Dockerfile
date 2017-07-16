FROM centos:centos7

# This will fix the `Fully-qualified class name does not match` issue.
# Uncomment the following two lines and rebuild the image.
#
# ENV LANG en_US.UTF-8
# ENV LC_ALL en_US.UTF-8

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
