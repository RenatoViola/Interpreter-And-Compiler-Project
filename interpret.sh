#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path_to_input_file>"
    exit 1
fi

INPUT_FILE="$1"
BUILD_DIR="./build"

# build sources
./build.sh

echo "Running the Java program..."
java -cp "$BUILD_DIR" main.ExecutableInterpreter "$INPUT_FILE"