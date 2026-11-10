#!/bin/bash

SRC_DIR="./src"
BUILD_DIR="./build"
PARSER_PATH="./src/parser"

mkdir -p "$BUILD_DIR"

echo "Cleaning up old Java files in the parser directory..."
find "$PARSER_PATH" -name "*.java" -exec rm {} \;

echo "Generating Java source from JavaCC grammar..."
javacc -OUTPUT_DIRECTORY=$PARSER_PATH $PARSER_PATH/Parser.jj

echo "Compiling Java source files..."
find "$SRC_DIR" -name "*.java" > sources.txt
javac @sources.txt -d "$BUILD_DIR"
rm sources.txt