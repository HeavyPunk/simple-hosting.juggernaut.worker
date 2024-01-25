FROM ubuntu:22.04

RUN apt update -y && apt install openjdk-17-jre -y

COPY build/ /juggernaut/

CMD java -jar /juggernaut/*.jar
