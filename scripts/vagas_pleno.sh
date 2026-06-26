#!/bin/bash

SID=$(curl -s -D - -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-06-18","capabilities":{},"clientInfo":{"name":"x","version":"1"}}}' | grep -i "mcp-session-id:" | tr -d '\r' | awk '{print $2}')

echo "Buscando vagas Pleno..."

echo ""
echo "=== JAVA SPRING BOOT PLENO ==="
curl -s -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SID" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"search_jobs","arguments":{"keywords":"Java Spring Boot","experience_level":"mid_senior","location":"Brazil","max_pages":2,"date_posted":"past_week","job_type":"full_time","work_type":"remote"}}}'

echo ""
echo "=== FULLSTACK PLENO ==="
curl -s -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SID" \
  -d '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"search_jobs","arguments":{"keywords":"fullstack developer","experience_level":"mid_senior","location":"Brazil","max_pages":2,"date_posted":"past_week","job_type":"full_time","work_type":"remote"}}}'

echo ""
echo "=== KAFKA ==="
curl -s -X POST http://localhost:8000/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: $SID" \
  -d '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"search_jobs","arguments":{"keywords":"Kafka Spring Boot","experience_level":"mid_senior","location":"Brazil","max_pages":2,"date_posted":"past_week","job_type":"full_time","work_type":"remote"}}}'
