
-- "currentPermits"  -- 当前桶里面的令牌数量
-- "maxPermits"         -- 最大能存放的令牌数量
-- "rate"                     -- 令牌生成速率， 例如 5 表示 1秒钟生成5个
-- 下一次可以获取令牌的时间点。例如当前通里面有5个令牌，能存放的桶数量是10，rate = 1
-- 假设在第10秒的时候A请求了5个后，还是同一时间B来请求X个令牌发现没有了，那么他需要等待  X / rate 秒
-- "nextCanGetTime"
local function reserveAndGetWaitLength(key , permits , nowMicros) -- permits请求需要的令牌个数，nowMicros请求时间
    local limitInfo = redis.call('HGET', key , "currentPermits", "maxPermits" , "rate" , "nextCanGetTime")
    local currentPermits = tonumber(limitInfo[1]);
    local maxPermits = tonumber(limitInfo[2]);
    local rate = tonumber(limitInfo[3]);
    local nextCanGetTime = tonumber(limitInfo[4]);
    -- 当前时间大于nextCanGetTime 表示 已经超过允许获取时间多少时间了，这时候更新下当前桶里面的数量
    if(nowMicros >= nextCanGetTime) then
        -- 除 1000000 是因为客户端传过来的是微秒，需要转成秒
        local temp =  (nowMicros - nextCanGetTime) / 1000000 * rate
        local newCurrentPermits = math.min(maxPermits , temp)
        redis.call('HSET',key , "currentPermits" , newCurrentPermits , "nextCanGetTime" , nowMicros)
    end
    nextCanGetTime = redis.call('HGET', key , "nextCanGetTime")
    currentPermits = redis.call('HGET', key , "currentPermits")
    local storedToSpend = math.min(currentPermits, permits) -- 实际取到的令牌数
    local freshPermits = permits - storedToSpend            -- 还差多少令牌数没取到
    local waitMicros =  (freshPermits / 5 ) * 1000000       --- 需要等待的微秒
    local newNextMicros = nextCanGetTime + waitMicros       --- 新的下一次取令牌的时间点
    currentPermits = currentPermits - storedToSpend         --- 重新计算当前桶里剩下的令牌数
    redis.call('HMSET', key, "currentPermits", currentPermits, "nextCanGetTime", newNextMicros)
    return newNextMicros
end

local function acquire(key, permits, nowMicros)
    local wait = reserveAndGetWaitLength(key, permits, nowMicros)
    return math.max(wait - nowMicros, 0)
end