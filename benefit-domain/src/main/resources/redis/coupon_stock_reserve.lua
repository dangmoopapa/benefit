-- KEYS[1] issued count
-- KEYS[2] user marker
-- ARGV[1] total quantity (empty = unlimited)
-- reply: OK:issued | ALREADY_ISSUED:issued | SOLD_OUT:issued
--
-- 총량(INCR)을 먼저 보고, 통과한 뒤에만 유저 marker(SET NX)를 본다.
-- 솔드아웃은 유저 키를 건드리지 않는다.

local issued_key = KEYS[1]
local user_key = KEYS[2]
local total = ARGV[1]

local issued = redis.call('INCR', issued_key)
if total ~= '' and issued > tonumber(total) then
  redis.call('DECR', issued_key)
  return 'SOLD_OUT:' .. total
end

if redis.call('SET', user_key, '1', 'NX') ~= true then
  redis.call('DECR', issued_key)
  return 'ALREADY_ISSUED:' .. (issued - 1)
end

return 'OK:' .. issued
