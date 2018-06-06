#!/usr/bin/env bash
wrk -t2 -c256 -d10s -T5 --script=./wrk.lua --latency http://localhost:8087/invoke
wrk -t2 -c256 -d60s -T5 --script=./wrk.lua --latency http://localhost:8087/invoke
-- wrk -t2 -c512 -d10s -T5 --script=./wrk.lua --latency http://localhost:8087/invoke
-- wrk -t2 -c512 -d60s -T5 --script=./wrk.lua --latency http://localhost:8087/invoke