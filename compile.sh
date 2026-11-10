#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path_to_input_file>"
    exit 1
fi

INPUT_FILE="$1"
BUILD_DIR="./build"

# build sources
./build.sh

echo "Running the compiler..."
java -cp "$BUILD_DIR" main.ExecutableCompiler "$INPUT_FILE"

# Directory paths
JASMIN_JAR=jasmin.jar
JFILES_DIR=src/jasmin/jfiles/
CLASSFILES_DIR=src/jasmin/classes/
mkdir -p $CLASSFILES_DIR

find "$CLASSFILES_DIR" -name "*.class" -exec rm {} \;

# Compile each .j file in the jfiles directory
# shellcheck disable=SC2231
for jfile in $JFILES_DIR*.j; do
    java -jar $JASMIN_JAR -d $CLASSFILES_DIR $jfile
done

cd $CLASSFILES_DIR
echo "Running decompiled files..."
java -cp . MicroML