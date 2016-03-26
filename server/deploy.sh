#!/usr/bin/env bash

mkdir -p trarget
curl "http://iotdk.intel.com/repos/2.0/java/iotdk-java-latest.zip" -o "target/iotdk-java-latest.zip"
unzip target/iotdk-java-latest.zip