#!/bin/bash
protoc utils/protos/market.proto --java_out=src/main/java

echo
echo "Premi INVIO per uscire..."
read