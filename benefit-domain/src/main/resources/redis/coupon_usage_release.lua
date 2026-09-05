-- KEYS[1] usage total count
-- reply: remaining after release (0 if missing / floored)

local usage_key = KEYS[1]
if redis.call('EXISTS', usage_key) == 0 then
  return 0
end

local used = redis.call('DECR', usage_key)
if used < 0 then
  redis.call('SET', usage_key, 0)
  return 0
end
return used
