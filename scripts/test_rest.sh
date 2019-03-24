#!/bin/env sh

URL="0.0.0.0:8080"
ACCESS_TOKEN="0"

curl --header "Authorization: Bearer ${ACCESS_TOKEN}" $2 "${URL}$1"
