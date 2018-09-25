package com.stereodustparticles.musicrequestsystem.mri;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class RequestList {
	private Request[] requests;
	private ArrayDeque<Request> more;
	
	public RequestList(ArrayList<Request> list) {
		requests = new Request[4];
		requests[0]=null;
		requests[1]=null;
		requests[2]=null;
		requests[3]=null;
		more = new ArrayDeque<>();
		int count = 0;
		for(Request r : list) {
			if(count >= 4) {
				break;
			}
			requests[count]=r;
			count++;
		}
		while(count > 0) {
			list.remove(0);
			count--;
		}
		more.addAll(list);
	}
	public RequestList() {
		this(new ArrayList<>());
	}
	
	public void add(Request r) {
		boolean found = false;
		for(Request e : requests) {
			if(e != null && e.equals(r)) {
				found = true;
			}
		}
		for(Request e : more) {
			if(e.equals(r)) {
				found = true;
			}
		}
		if(found == false && r.getStatus() <= 1) {
			if(requests[0] == null) {
				requests[0]=r;
			}
			else if(requests[1] == null) {
				requests[1]=r;
			}
			else if(requests[2] == null) {
				requests[2]=r;
			}
			else if(requests[3] == null) {
				requests[3]=r;
			}
			else {
				more.add(r);
			}
		}
	}
	
	public void remove(int i) throws Exception {
		if(i < 0 || i > 3) {
			throw new Exception("Invalid index: " + i);
		}
		requests[i]=more.poll();
	}
	
	public Request get(int i) throws Exception {
		if(i < 0 || i > 3) {
			throw new Exception("Invalid index: " + i);
		}
		return requests[i];
	}
	
	public int getAdditionalRequests() {
		return more.size();
	}
	
	public void clean() throws Exception {
		Iterator<Request> it = more.iterator();
		while(it.hasNext()) {
			if(it.next().getStatus() > 1) {
				it.remove();
			}
		}
		for(int i = 0; i < 4; i++) {
			if(requests[i] != null && requests[i].getStatus() > 1) {
				remove(i);
			}
		}
	}
}
