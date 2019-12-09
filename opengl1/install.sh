#!/bin/bash

sudo apt-get update -y
sudo apt-get install openjdk-8-jdk -y

sudo ./gradlew jar