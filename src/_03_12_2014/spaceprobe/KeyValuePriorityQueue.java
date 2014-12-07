package _03_12_2014.spaceprobe;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class KeyValuePriorityQueue <K extends Comparable<K>, V>{

	MultiValueMap<K, V> hm;
	PriorityQueue<K> pq;
	
	public KeyValuePriorityQueue(){
		pq = new PriorityQueue<K>();
		hm = new MultiValueMap<K, V>();
	}
	
	public boolean add(K key, V value){
		hm.put(key, value);
		return pq.add(key);
	}
	
	public V peek(){
		LinkedList<V> collection = (LinkedList<V>) hm.getCollection(pq.peek());
		if(collection != null && collection.size() > 0){
			return collection.getFirst();
		}
		else{
			return null;
		}
	}
	
	public V remove(){
		if(pq.size() > 0){
			return hm.removeFirstFromCollection(pq.remove());
		}
		else{
			return null;
		}
		
	}
}
