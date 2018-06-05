wrk -t2 -c250 -d20s -T5 --script=./wrk.lua --latency http://localhost:8087/invoke
wrk -t2 -c250 -d60s -T5 --script=./wrk.lua --latency http://localhost:8087/invoke