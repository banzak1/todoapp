#!/bin/bash
set -e

# Init session
RESP=$(curl -s -D - -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-06-18","capabilities":{},"clientInfo":{"name":"x","version":"1"}}}')

SID=$(echo "$RESP" | grep -i "mcp-session-id:" | tr -d '\r' | awk 'END{print $NF}')
echo "Session: $SID"

OUTFILE="/mnt/c/Users/lsant/devBanzak/todoapp/scripts/jobs_output.txt"

# Search Java Spring Boot Pleno
echo "=== JAVA SPRING BOOT PLENO ===" > "$OUTFILE"
curl -s -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SID" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"search_jobs","arguments":{"keywords":"Java Spring Boot","experience_level":"mid_senior","location":"Brazil","max_pages":1,"date_posted":"past_week","job_type":"full_time","work_type":"remote"}}}' >> "$OUTFILE" 2>&1

echo "" >> "$OUTFILE"
echo "=== FULLSTACK ANGULAR ===" >> "$OUTFILE"
curl -s -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SID" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"search_jobs","arguments":{"keywords":"fullstack Angular Java","experience_level":"mid_senior","location":"Brazil","max_pages":1,"date_posted":"past_week","job_type":"full_time","work_type":"remote"}}}' >> "$OUTFILE" 2>&1

echo "Done! Output saved to scripts/jobs_output.txt"
