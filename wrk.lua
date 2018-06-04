done = function(summary, latency, requests)
   
    io.write("--------------------------\n")
    local durations=summary.duration / 1000000    -- 执行时间，单位是秒
    local errors=summary.errors.status            -- http status不是200，300开头的
    local requests=summary.requests               -- 总的请求数
    local valid=requests-errors                   -- 有效请求数=总请求数-error请求数


    io.write("Durations:       "..string.format("%.2f",durations).."s".."\n")
    io.write("Requests:        "..summary.requests.."\n")
    io.write("Avg RT:          "..string.format("%.2f",latency.mean / 1000).."ms".."\n")
    io.write("Max RT:          "..(latency.max / 1000).."ms".."\n")
    io.write("Min RT:          "..(latency.min / 1000).."ms".."\n")
    io.write("Error requests:  "..errors.."\n")
    io.write("Valid requests:  "..valid.."\n")
    io.write("QPS:             "..string.format("%.2f",valid / durations).."\n")
    io.write("--------------------------\n")

end
