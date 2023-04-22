#!/bin/bash

######################
# For output like
#
# > time="2023-04-22T01:47:05Z" level=warning msg="Request Failed" error="Get \"http://application:8080/hello\": dial tcp 172.18.0.3:8080: connect: connection refused"
# > time="2023-04-22T01:47:05Z" level=info msg="Service hasn't started yet, sleeping for 1s" source=console
# > time="2023-04-22T01:47:06Z" level=warning msg="Request Failed" error="Get \"http://application:8080/hello\": dial tcp 172.18.0.3:8080: connect: connection refused"
# > time="2023-04-22T01:47:06Z" level=info msg="Service hasn't started yet, sleeping for 1s" source=console
# > time="2023-04-22T01:47:08Z" level=info msg="Service is ready" source=console
# >      ✓ is status 200
# >
# >      █ setup
# >
# >      checks...............: 100.00% ✓ 27982      ✗ 0
# >      data_received........: 11 MB   341 kB/s
# >      data_sent............: 2.4 MB  71 kB/s
# >      iteration_duration...: avg=538.1ms p(99)=4.13s p(99.9)=6.18s p(99.99)=6.43s max=6.46s
# >      iterations...........: 27982   838.947264/s
#
# captures only results and pushes them to Github output

# Read input from stdin
output=$(cat)

# Find the line containing the earliest "checks..."
checks_line=$(echo "$output" | grep -n "checks..." | head -1)

# Get the line number of the match
line_number=$(echo "$checks_line" | cut -d ':' -f 1)

# Remove the lines before the "checks..." line, remove leading whitespace, and append the result to Github Output
results=$(echo "$output" | tail -n +$((line_number+1)) | sed 's/^[[:space:]]*//')
echo $results
echo "results=$results" >> $GITHUB_OUTPUT

