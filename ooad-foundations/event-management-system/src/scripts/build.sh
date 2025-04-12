#!/bin/bash
set -e
echo "Compiling the OOAD project..."
mkdir -p out
javac --enable-preview --release 21 \
    -d out \
    --module-source-path src/modules \
    --module com.example.eventmanagement.oop
echo "Build complete."
