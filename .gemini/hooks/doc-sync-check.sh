#!/usr/bin/env bash

# Documentation Sync Hook
# Checks if core logic changed but GEMINI.md was not updated.

# Check if GameAnalyzer.java changed in the last commit (or just changed in worktree)
changed_logic=$(git diff --name-only HEAD | grep "GameAnalyzer.java")
changed_docs=$(git diff --name-only HEAD | grep "GEMINI.md")

if [ ! -z "$changed_logic" ] && [ -z "$changed_docs" ]; then
    # We don't want to BLOCK the agent, but we want to inform.
    # Actually, we'll return a reason to inform.
    echo '{"decision": "allow", "message": "Note: You changed GameAnalyzer.java but did not update GEMINI.md. Please ensure the documentation is still accurate."}'
    exit 0
else
    echo '{"decision": "allow"}'
    exit 0
fi
