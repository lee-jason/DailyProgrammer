package _03_12_2014.spaceprobe;

import java.util.HashMap;
import java.util.PriorityQueue;

public class KeyValuePriorityQueue <K, V>{

	HashMap<K, V> hm;
	PriorityQueue<K> pq;
	
	public KeyValuePriorityQueue(){
		pq = new PriorityQueue<K>();
		hm = new HashMap<K, V>();
	}
	
	public boolean add(K key, V value){
		hm.put(key, value);
		return pq.add(key);
	}
	
	public V peek(){
		return hm.get(pq.peek());
	}
	
	public V remove(){
		return hm.remove(pq.remove());
	}
}
