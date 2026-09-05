-- KEYS[1] issued count
-- KEYS[2] user marker
-- ARGV[1] total quantity (empty = unlimited)
-- reply: already|issued|remaining  (remaining=-1 if unlimited)

local issued = tonumber(redis.call('GET', KEYS[1]) or '0')
local already = redis.call('EXISTS', KEYS[2])
local remaining = -1
if ARGV[1] ~= '' then
  remaining = math.max(tonumber(ARGV[1]) - issued, 0)
end
return already .. '|' .. issued .. '|' .. remaining
