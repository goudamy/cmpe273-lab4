package edu.sjsu.cmpe.cache.client;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DistributedCacheService implements CacheServiceInterface {
	 private final String cacheServerUrl;
    private Future<HttpResponse<JsonNode>> future=null;
    private int respCode;
    private final CountDownLatch sleeper = new CountDownLatch(3);

    public DistributedCacheService(String serverUrl) {
        this.cacheServerUrl = serverUrl;      
    
    }
    @Override
    public void put(long key, String value) {
    	HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .put(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value"
                    		+ ""
                    		+ "", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 200) {
            System.out.println("Failed to add to the cache.");
        }
    }
    @Override
    public String get(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    @Override
    public void delete(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .delete(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 204) {
            System.out.println("Failed to delete "+key+" from the cache.");
        }else {
            System.out.println("Delete " + key + " from " + this.cacheServerUrl);
        }  
    }
    
    @Override
    public void asyncPut(long key, String value,final ConcurrentHashMap<String,String> Mapper) {
    	
    	 future = Unirest.put(this.cacheServerUrl + "/cache/{key}/{value}")
	    			.header("accept", "application/json")
	                .routeParam("key", Long.toString(key))
	                .routeParam("value", value)
    			    .asJsonAsync(new Callback<JsonNode>() {

    			    public void failed(UnirestException e) {
    			    	System.out.println("The request has failed"); 			    	
    			    
    			    }

    			    public void completed(HttpResponse<JsonNode> response) {
    			         int code = response.getCode() ; 
    			        String coder = Integer.toString(code);
    			         Map<String, List<String>> headers = response.getHeaders();    			         
    			         JsonNode body = response.getBody();
    			         InputStream rawBody = response.getRawBody();
    			         System.out.println("request successful"); 
    			         System.out.println("checking"+ coder);
    			         Mapper.put(cacheServerUrl, coder);
    			         sleeper.countDown();
    			       
    			    }

    			    public void cancelled() {
    			        System.out.println("The request has been cancelled");
    			    }

    			}); 
    	 
    	
    }
         @Override
         public int coder()
         {
    	int resp =0;
       	try{
    	 HttpResponse<JsonNode> response=future.get(200,TimeUnit.MILLISECONDS);
    	 resp=response.getCode();
       	}
       	catch (Exception  e) {
       	System.out.println("code not found")	;
       	}
    	   return resp;
       	
    }
    
    @Override
    public void getVal(long key,final ConcurrentHashMap<String,String> Mapper,final CountDownLatch sleeping) {
    	
    	
    	future = Unirest.get(this.cacheServerUrl + "/cache/{key}")
    			.header("accept", "application/json")
                .routeParam("key", Long.toString(key))
                .asJsonAsync(new Callback<JsonNode>() {

			    public void failed(UnirestException e) {    			    	
			        System.out.println("The request has failed"); 			       
			    		    }

			    public void completed(HttpResponse<JsonNode> response) {
			    	 String value="0";
			         int code = response.getCode() ;    			        
			         if (code == 200){
			         value = response.getBody().getObject().getString("value");
			         System.out.println("response body in cacheServerUrl is"+ value);
			         }
			         Mapper.put(cacheServerUrl, value);
			         sleeping.countDown();
			       	}

			    public void cancelled() {
			        System.out.println("The request has been cancelled");
			    }

			});   
}
}