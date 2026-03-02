#!/usr/bin/env bash

# AI Performance Regression Hook
# Runs core game logic tests before allowing a commit.

# Placeholder check for JUnit tests (once added)
# For now, it just ensures GameAnalyzer and MiniMax exist.

if [ ! -f TTTGUI/src/GameAnalyzer.java ] || [ ! -f TTTGUI/src/MiniMax.java ]; then
    echo '{"decision": "deny", "reason": "Critical game logic files are missing."}'
    exit 0
fi

# In a real scenario, this would run:
# java -cp TTTGUI/out/production/TTTGUI:junit.jar org.junit.runner.JUnitCore GameLogicTestSuite
# For now, we'll just check if it compiles.

javac TTTGUI/src/*.java -d TTTGUI/out/production/TTTGUI 2> /dev/null

if [ $? -eq 0 ]; then
    echo '{"decision": "allow"}'
    exit 0
else
    echo '{"decision": "deny", "reason": "Regression check failed: Game logic does not compile."}'
    exit 0
fi
