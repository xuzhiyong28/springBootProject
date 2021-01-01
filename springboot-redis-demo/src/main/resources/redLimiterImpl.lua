local storedPermits = 'storedPermits' -- 当前存储的令牌数
local maxPermits = 'maxPermits' -- 最大可存储的令牌数
local stableIntervalMicros = 'stableIntervalMicros' -- 多久产生一个令牌
local nextFreeTicketMicros = 'nextFreeTicketMicros' -- 下一次可以获取令牌的时间点

    local function reserveAndGetWaitLength(key, permits, nowMicros)
    local limiterInfo = redis.call('HMGET', key, storedPermits, maxPermits, stableIntervalMicros, nextFreeTicketMicros)
    local stored = tonumber(limiterInfo[1])
    local max = tonumber(limiterInfo[2])
    local interval = tonumber(limiterInfo[3])
    local nextMicros = tonumber(limiterInfo[4])


    if (nowMicros >= nextMicros) then
        local newPermits = (nowMicros - nextMicros) / interval  -- 最关键代码：根据时间差计算令牌数量并匀速的放入令牌
        local newStored = math.min(max, stored + newPermits)
        redis.call('HMSET', key, storedPermits, newStored, nextFreeTicketMicros, nowMicros)
    end

    nextMicros = tonumber(redis.call('HGET', key, nextFreeTicketMicros))
    stored = tonumber(redis.call('HGET', key, storedPermits))
    local storedToSpend = math.min(stored, permits) -- 需要花费的令牌，permits表示要获取的令牌数量
    local freshPermits = permits - storedToSpend  -- freshPermits 实际花费的令牌 可能是0
    local waitMicros = freshPermits * interval  -- 需要等待的时间
    local newNextMicros = nextMicros + waitMicros
    stored = stored - storedToSpend
    redis.call('HMSET', key, storedPermits, stored, nextFreeTicketMicros, newNextMicros)
    return newNextMicros
end

local function acquire(key, permits, nowMicros)
    local wait = reserveAndGetWaitLength(key, permits, nowMicros)
    return math.max(wait - nowMicros, 0)
end
-- permits qps
-- nowMicros 当前时间
-- timeoutMicros 超时时间
local function tryAcquire(key, permits, nowMicros, timeoutMicros)
    local next = tonumber(redis.call('HGET', key, nextFreeTicketMicros)) --下一次可以获取令牌的时间点
    if (nowMicros + timeoutMicros <= next)
    then
        -- tryAcquire false
        return -1
    else
        local wait = reserveAndGetWaitLength(key, permits, nowMicros)
        return math.max(wait - nowMicros, 0)
    end
end
-- ARGV[2] qps -- 需要获取的令牌数量
-- ARGV[3] 当前时间
-- ARGV[4] 超时时间

local key = KEYS[1]
local method = ARGV[1]
if method == 'acquire' then
    return acquire(key, tonumber(ARGV[2]), tonumber(ARGV[3]))
elseif method == 'tryAcquire' then
    return tryAcquire(key, tonumber(ARGV[2]), tonumber(ARGV[3]), tonumber(ARGV[4]))
end