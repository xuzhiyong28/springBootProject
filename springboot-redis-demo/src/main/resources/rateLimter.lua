-- 获取KEY
local key1 = KEYS[1]
local val = redis.call('incr', key1)
-- 获取key1剩下的时间
local ttl = redis.call('ttl',key1)

--获取ARGV内的参数并打印
-- ARGV[1]超时时间
local expire = ARGV[1]
-- ARGV[2]单位时间允许通过的请求数
local times = ARGV[2]

-- 日志打印 没什么用
redis.log(redis.LOG_DEBUG,tostring(times))
redis.log(redis.LOG_DEBUG,tostring(expire))
redis.log(redis.LOG_NOTICE, "incr "..key1.." "..val);

if val == 1 then
    redis.call('expire',key1,tonumber(expire))
else
    if ttl == -1 then
        redis.call('expire',key1,tonumber(expire))
    end
end

if val > tonumber(times) then
    return 0
end
    return 1