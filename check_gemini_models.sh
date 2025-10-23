#!/bin/bash
# Script to check available Gemini models with your API key
# Usage: ./check_gemini_models.sh YOUR_API_KEY

API_KEY=$1

if [ -z "$API_KEY" ]; then
    echo "Usage: ./check_gemini_models.sh YOUR_API_KEY"
    echo "Example: ./check_gemini_models.sh AIzaSy..."
    exit 1
fi

echo "Checking available Gemini models..."
echo "=================================="
echo ""

curl -s "https://generativelanguage.googleapis.com/v1beta/models?key=${API_KEY}" \
  | grep -E '"name"|"displayName"' \
  | sed 's/models\///' \
  | sed 's/"name": "//g' \
  | sed 's/"displayName": "//g' \
  | sed 's/",//g' \
  | sed 's/"//g' \
  | sed 's/^[[:space:]]*//'

echo ""
echo "=================================="
echo "Use one of these model names in your configuration"
