#!/bin/bash

echo "Hangar Integration Test - Maven"
echo "-------------------------------"

export HANGAR_URL="127.0.0.1:8080"

function test_execution {

    "$@"
    local status=$?
    if [ $status -ne 0 ]; then
        echo "error with $1" >&2
    fi
}

start1=`date +%s`
test_execution mvn -s settings.xml -f ./project-1/pom.xml package 
end1=`date +%s`
echo "Test 1 - Package Snapshot : " $((end1-start1))"s"



