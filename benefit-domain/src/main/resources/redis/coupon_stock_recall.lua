-- KEYS[1] issued count
-- KEYS[2] user marker
-- reply: issued count

if redis.call('DEL', KEYS[2]) == 1 then
  local issued = tonumber(redis.call('GET', KEYS[1]) or '0')
  if issued > 0 then
    return redis.call('DECR', KEYS[1])
  end
end
return tonumber(redis.call('GET', KEYS[1]) or '0')
