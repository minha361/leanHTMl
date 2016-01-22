package com.adr.bigdata.indexingrd.models;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public abstract class RdCachedModel extends AbstractModel {
	public static final String LAST_UPDATE_WHPM_KEYS = "changed_docids";
	private JedisPool jedisPool;
	private JedisPool outputJedisPool;
	private int jedisDb = 0;// main redis db
	private int ouptuCacheDb = 0;// output cache redis db

	public int getJedisDb() {
		return jedisDb;
	}

	public void setJedisDb(int jedisDb) {
		this.jedisDb = jedisDb;
	}

	public int getOuptuCacheDb() {
		return ouptuCacheDb;
	}

	public void setOuptuCacheDb(int ouptuCacheDb) {
		this.ouptuCacheDb = ouptuCacheDb;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public JedisPool getOutputJedisPool() {
		return outputJedisPool;
	}

	protected Jedis getJedis() {
		Jedis j = jedisPool.getResource();
		j.select(jedisDb);
		return j;
	}

	protected Jedis getJedisUpdateTime() {
		Jedis j = jedisPool.getResource();
		j.select(jedisDb);
		return j;
	}

	protected Jedis getOutputJedis() {
		Jedis j = outputJedisPool.getResource();
		j.select(ouptuCacheDb);
		return j;
	}

	public void setOutputJedisPool(JedisPool outputJedisPool) {
		this.outputJedisPool = outputJedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public void sendLastUpdatedWhpId(int whpmID) {
		try (Jedis j = jedisPool.getResource()) {
			j.select(jedisDb);
			j.lpush(LAST_UPDATE_WHPM_KEYS, String.valueOf(whpmID));
		}
	}

	public void sendLastUpdatedWhpIds(Collection<String> whpmIDs) {
		try (Jedis j = jedisPool.getResource()) {
			j.select(jedisDb);
			j.lpush(LAST_UPDATE_WHPM_KEYS, whpmIDs.toArray(new String[whpmIDs.size()]));
		}
	}

	public void sendLastUpdatedWhpIds(String... whpmIDs) {
		try (Jedis j = jedisPool.getResource()) {
			j.select(jedisDb);
			j.lpush(LAST_UPDATE_WHPM_KEYS, whpmIDs);
		}
	}
	
	public void sendLastUpdatedWhpIdsInteger(Collection<Integer> warehouseProductItemMappingIds) {
		List<String> wpimIdList = warehouseProductItemMappingIds.stream().map(Object::toString).collect(Collectors.toList());
		sendLastUpdatedWhpIds(wpimIdList);
	}

	public String getLastUpdatedWhpId() {
		try (Jedis j = jedisPool.getResource()) {
			j.select(jedisDb);
			return j.rpop(LAST_UPDATE_WHPM_KEYS);
		}
	}

	public void put(String whpmID, Map<String, String> data) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.select(jedisDb);
			jedis.hmset(whpmID, data);
		}
	}

	public Map<String, String> get(String whpmID) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.select(this.jedisDb);
			return jedis.hgetAll(whpmID);
		}
	}

	protected String keyOfWPM(int wpmId) {
		return "wpm:" + wpmId;
	}
	
	public Map<String, String> getTimes(String key, Collection<String> fields){
		String[] fieldArr = (String[]) fields.toArray();
		return getTimes(key, fieldArr);
	}
	
	/**
	 * @param key is cache name : Landing page, Brand ....
	 * @param fields is wpimIds
	 * @return
	 */
	public Map<String, String> getTimes(String key, String... fields){
		try {
			Jedis jedis = getJedisUpdateTime();
			ArrayList<String> evalStringList = new ArrayList<String>();
			evalStringList.add(key);
			if(fields != null && fields.length > 0){
				for(String filed : fields){
					evalStringList.add(filed);
				}
			}
			String[] evalStringArr = (String[]) evalStringList.toArray();
			
			@SuppressWarnings("unchecked")
			ArrayList<String> returnStrings = (ArrayList<String>) jedis.eval(getResourceFile("lua/getTimes.lua"), 1, evalStringArr);
			if(fields != null){
				Map<String, String> returnMap = new HashMap<String, String>();
				for(int i = 0; i < fields.length; i++){
					String value = returnStrings.get(i);
					if(value != null){
						returnMap.put(fields[i], value);
					}
				}
				return returnMap;
			} else {
				return new HashMap<String, String>();
			}
		} catch (Exception e) {
			getLogger().error("", e);
			return new HashMap<String, String>();
		}
	}
	
	private static String getResourceFile(String fileName) throws IOException {
		URL url = Resources.getResource(fileName);
		String text = Resources.toString(url, Charsets.UTF_8);
		return text;
	}

}
