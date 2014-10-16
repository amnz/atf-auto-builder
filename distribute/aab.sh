#!/bin/sh

# ------------- please set enviroments -------------------------------
# JAVA_HOME="jdk/x64/1.7.0_60"
AAB_DIR=`dirname $0`
GPSS_LIB_DIRS="${AAB_DIR}/libs"
GPSS_OPT=""

# ------------- check Java Home --------------------------------------
if [ -z "$JAVA_HOME" ] ;  then
  echo "You must set JAVA_HOME to point at your Java Development Kit installation"
  exit 1
fi

# ------------- set GPSS Options -------------------------------------
GPSS_LIB_DIRS="-Djava.ext.dirs=${GPSS_LIB_DIRS}"

if [ -d ${JAVA_HOME}/jre ] ; then
  __SERVER_JVM_PATH=${JAVA_HOME}/jre/lib/i386/server
  GPSS_LIB_DIRS="${GPSS_LIB_DIRS}:${JAVA_HOME}/jre/lib/ext"
else
  __SERVER_JVM_PATH=${JAVA_HOME}/lib/i386/server
  GPSS_LIB_DIRS="${GPSS_LIB_DIRS}:${JAVA_HOME}/lib/ext"
fi

GPSS_OPT="$GPSS_OPT ${GPSS_LIB_DIRS}"

# ------------- set classpath ----------------------------------------
${JAVA_HOME}/bin/java ${GPSS_OPT} -jar ${AAB_DIR}/atf-auto-builder.jar $@
