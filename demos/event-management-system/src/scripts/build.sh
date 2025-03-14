#!/bin/bash
# scripts/build.sh
set -e
echo "Compiling the project..."
mkdir -p out
javac --enable-preview --release 21 \
    -d out \
    --module-source-path src/modules \
    --module com.example.core.common,com.example.eventmanagement.domain,com.example.eventmanagement.operations,com.example.eventmanagement.app
echo "Build complete."
