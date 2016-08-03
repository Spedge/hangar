#!/bin/bash

echo "Hangar Integration Test - Pip"
echo "-----------------------------"

function spinner()
{
    local delay=0.75
    local spinstr='|/-\'
    while true; do
        local temp=${spinstr#?}
        printf " [%c]  " "$spinstr"
        local spinstr=$temp${spinstr%"$temp"}
        sleep $delay
        printf "\b\b\b\b\b\b"
    done
}

function test_execution() 
{
	# Start the timer
	start1=`date +%s`
	
	# Execute the test in a background process
    "$@" 1> test.results 2> test.error &
	TEST_PID=$!
	
	# Now execute the spinner in a background process
	spinner &
	SPINNER_PID=$!
	
	# Set a trap to kill the spinner once the test is done
	trap 'kill $SPINNER_PID' 0
    
	# Set the wait for the test. 
    if wait $TEST_PID; then
	    end1=`date +%s`
		printf "\b\b\b\b\b[OK - %ss]" $((end1-start1))
	else
		printf "\b\b\b\b\b[ERROR]"
		cat test.results
		exit 1
	fi
}

# Scenario 1 - Simple Requirements
printf 'Scenario 1 - Package Snapshot...' 
#test_execution pip -r ./project-1/requirements.txt -i $HANGAR_URL
pip install -r ./project-1/requirements.txt -i http://$HANGAR_URL:8080/python/simple --trusted-host $HANGAR_URL
printf "\n---- System Tests\n"
