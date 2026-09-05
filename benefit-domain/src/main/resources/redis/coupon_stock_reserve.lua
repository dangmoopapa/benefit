-- KEYS[1] issued count
-- KEYS[2] user marker
-- ARGV[1] total quantity (empty = unlimited)
-- reply: OK|issued | ALREADY_ISSUED|issued | SOLD_OUT|issued

local issued_key = KEYS[1]
local user_key = KEYS[2]

if redis.call('EXISTS', user_key) == 1 then
  return 'ALREADY_ISSUED|' .. (redis.call('GET', issued_key) or '0')
end

if ARGV[1] ~= '' then
  local issued = redis.call('INCR', issued_key)
  if issued > tonumber(ARGV[1]) then
    redis.call('DECR', issued_key)
    return 'SOLD_OUT|' .. ARGV[1]
  end
else
  redis.call('INCR', issued_key)
end

redis.call('SET', user_key, '1')
return 'OK|' .. redis.call('GET', issued_key)
