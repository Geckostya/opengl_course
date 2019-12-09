#!/bin/bash

file=/etc/java-8-openjdk/accessibility.properties

sudo sed -i 's/^assistive_technologies=org.GNOME.Accessibility.AtkWrapper/#&/' $file

/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar build/libs/opengl1-1.0-SNAPSHOT.jar

sudo sed -i '/^#assistive_technologies=org.GNOME.Accessibility.AtkWrapper/s/^#//' $file