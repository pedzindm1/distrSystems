# run this script from /out/production/ds-hw to start the servers and redirect output to log files for each server

java Server < input/server1.txt > server1.log 2>&1 &
java Server < input/server2.txt > server2.log 2>&1 &
