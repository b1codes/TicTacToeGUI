#!/usr/bin/env bash

# Build Verification Hook
# Runs javac after a file is written to ensure the project still compiles.

# Read tool input/output (though we don't strictly need it for this check)
input=$(cat)

# Compile the project
javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI 2> .build_errors.log

if [ $? -eq 0 ]; then
    rm -f .build_errors.log
    echo '{"decision": "allow"}'
    exit 0
else
    errors=$(cat .build_errors.log | head -n 10)
    rm -f .build_errors.log
    echo "{"decision": "deny", "reason": "Build failed after change. Errors:
$errors"}"
    exit 0 # We still exit 0 from the script to provide a valid JSON response
fi
