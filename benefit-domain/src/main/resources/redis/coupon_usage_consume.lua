-- KEYS[1] usage total count
-- ARGV[1] total limit
-- reply: 1 ok | 0 limit exceeded

local usage_key = KEYS[1]
local limit = tonumber(ARGV[1])

local used = redis.call('INCR', usage_key)
if used > limit then
  redis.call('DECR', usage_key)
  return 0
end
return 1
