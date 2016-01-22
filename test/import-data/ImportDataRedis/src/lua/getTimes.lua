local length = table.getn(ARGV)
local returnArr = {}
for i= 1, length do
   returnArr[i] = redis.call('hget',KEYS[1], ARGV[i])
end
return returnArr