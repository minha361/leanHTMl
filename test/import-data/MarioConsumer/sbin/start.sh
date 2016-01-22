#!/bin/bash

MAIN_CLASS=com.mario.consumer.MarioConsumer

kill $(jps -l | grep $MAIN_CLASS | awk '{print $1}');

WORKING_DIR=${PWD}

$(java -version)

echo "Working directory: $WORKING_DIR"

# test -e /opt/mario-consumer && mv /opt/mario-consumer /opt/mario-consumer.$(date +%Y%m%d%H%M%S);

GC_TUNE="-XX:+UseParNewGC \
-XX:NewRatio=3 \
-XX:SurvivorRatio=4 \
-XX:TargetSurvivorRatio=90 \
-XX:MaxTenuringThreshold=8 \
-XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC \
-XX:ConcGCThreads=4 -XX:ParallelGCThreads=4 \
-XX:+CMSScavengeBeforeRemark \
-XX:PretenureSizeThreshold=64m \
-XX:+UseCMSInitiatingOccupancyOnly \
-XX:CMSInitiatingOccupancyFraction=50 \
-XX:CMSMaxAbortablePrecleanTime=6000 \
-XX:+CMSParallelRemarkEnabled \
-XX:+ParallelRefProcEnabled"

JVM_OPTS="$GC_TUNE \
-server \
-d64 -Xms4g -Xmx4g \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath='logs/dump-$(date +%Y%m%d%H%M%S).hprof' \
-Dlog4j.configurationFile=conf/log4j2.xml"

CLASS_PATH=".:./lib/*:./extensions/__lib__/*:"

CMD=$(echo "$JVM_OPTS -cp $CLASS_PATH $MAIN_CLASS $@")

echo "Starting application with command: $CMD"

java $JVM_OPTS -cp $CLASS_PATH $MAIN_CLASS $@ > /dev/null 2>&1 &