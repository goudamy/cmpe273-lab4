package edu.sjsu.cmpe.cache.client;
import com.mashape.unirest.http.Unirest;
import java.util.*;
public class Client {
    
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        List<String> cache = new ArrayList<String>();
	 	cache.add("http://localhost:3000");
	 	cache.add("http://localhost:3001");
	 	cache.add("http://localhost:3002");
       
        
        ClientDetails clientCrdt = new ClientDetails(cache);
        System.out.println("First HTTP PUT call to store key 1=>a,step:1");
        boolean ret = clientCrdt.put(1, "a");     
        System.out.println("status "+ ret);
        System.out.println("Second HTTP PUT call to update key 1 => b ; step:2");
        Thread.sleep(30000);
       
        ret = clientCrdt.put(1, "b");
        
        System.out.println("status "+ ret);
        System.out.println("start server noww..");
        Thread.sleep(30000);
        String  changedValue= clientCrdt.get(1);
        System.out.println("Final HTTP GET call to retrieve key =>1 value,step3 " + changedValue);
        System.out.println("shutting down");
        Unirest.shutdown();
    }

}