#!/usr/bin/env bash

rm -rf target
sdk install java 23.r20-nik && sdk default java 23.r20-nik
./mvnw -Pnative -DskipTests  native:compile  && ./target/java21
