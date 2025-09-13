#!/bin/bash

KEY_LENGTH=32
FORMAT=base64

if [ "$FORMAT" == "base64" ]; then
  openssl rand -base64 "$KEY_LENGTH"
elif [ "$FORMAT" == "hex" ]; then
  openssl rand -hex "$KEY_LENGTH"
else
  echo "Unsupported format: $FORMAT"
  exit 1
fi
