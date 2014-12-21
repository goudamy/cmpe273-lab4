package edu.sjsu.cmpe.cache.client;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;


public class ClientDetails  {

	private final CountDownLatch sleeper = new CountDownLatch(3);
    private List<CacheServiceInterface> cacheList = new ArrayList<CacheServiceInterface>();
    private final ConcurrentHashMap<String, String> hashmap = new ConcurrentHashMap<String, String>();
    private final ConcurrentHashMap<String, String> hashmap1 = new ConcurrentHashMap<String, String>();
 	protected int count = 0;
   

    public ClientDetails(List<String> cache2) {

    	List<String> cach1 = cache2;
		for (String c : cach1) {		 
			DistributedCacheService cache = new DistributedCacheService(c);
			cacheList.add(cache);
		}
    }
   
    public boolean put(long key, String value) throws InterruptedException {
   
		int respCode=0;
		boolean status = true;
		// int flag = 0;
       List<CacheServiceInterface> partialState = new ArrayList<CacheServiceInterface>();
    	for (CacheServiceInterface clientCache : cacheList) {
    		
		 clientCache.asyncPut(Long.valueOf(key), value,hashmap1);
			respCode=clientCache.coder();
		 
		    /*System.out.println(hashmap1);
			for (String val : hashmap1.keySet()) {
				String val1=hashmap1.get(val);
				System.out.println("valll"+val1);
			
					System.out.println(val);
					count = count + 1;
					System.out.println(count);
					partialState.add(clientCache);
		            	}*/
			if (respCode == 200) {
				count++;
				partialState.add(clientCache);
			}
								
					}
		  
	System.out.println(count);
	if (count <= 1) {
		for (CacheServiceInterface cache : partialState) {
			System.out.println(key);
			cache.delete(key);
			System.out.println("Failed state :Cache deleted and cleaning up...");
			status = false;
		}
	
		}
		return status;
    }

    
    
    
     public String get(long key) {		
		    	
 		
 		for (CacheServiceInterface cache : cacheList) {
 			cache.getVal(key, hashmap, sleeper);
 		}

 		HashMap<String, Integer> vals = new HashMap<String, Integer>();
 		
 		try{
 			sleeper.await();
 		}catch (Exception  e) {
        		
        	}
 		 String max = null;
  		 String finalNum = null;
  		 
 			for (String host : hashmap.keySet()) {
 			
 				finalNum = hashmap.get(host);
 				if (vals.containsKey(finalNum)) {
/*
 *               int numbe=vals.get(finalNum) + 1
 *               vals.put(finalNum,numbe);
 *					max = finalNum;
 */
 					vals.put(finalNum,vals.get(finalNum) + 1);
 					max = finalNum;
 				} else {
 					vals.put(finalNum, 1);
 				}
 			}
 		 		
 		for (String host : hashmap.keySet()) {
 			
 			finalNum = hashmap.get(host);
 			if (!finalNum.equals(max)) {
 				
 				DistributedCacheService cache = new DistributedCacheService(host);
 				System.out.println("The server that must be repaired is "+host );
 				cache.put(key, max);
 			}
 		}

 		return max;
 	}





}