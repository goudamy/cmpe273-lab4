package edu.sjsu.cmpe.cache.client;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public interface CacheServiceInterface {
	public String get(long key);
	public void asyncPut(long key, String value,final ConcurrentHashMap<String,String> valueMap);
	public void put(long key, String value);
    public int coder();
    public void delete(long key);
 
    public void getVal(long key, final ConcurrentHashMap<String,String> valueMap,final CountDownLatch sleeper);
}
