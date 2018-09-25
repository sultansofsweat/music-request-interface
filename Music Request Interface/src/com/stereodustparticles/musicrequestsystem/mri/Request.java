package com.stereodustparticles.musicrequestsystem.mri;

public class Request {
	private int id;
	private String user;
	private String request;
	private int status;
	private String comment;
	private String response;
	private String filename;
	
	public Request(int i, String u, String r, int s, String c, String p, String f) {
		id = i;
		user = u;
		request = r;
		status = s;
		comment = c;
		response = p;
		filename = f;
		//To fix the status stuff
		if(status == 2) {
			status = 1;
		}
		else if(status == 1) {
			status = 2;
		}
	}
	
	public int getID() {
		return id;
	}
	public String getUser() {
		return user;
	}
	public String getRequest() {
		return request;
	}
	public int getStatus() {
		return status;
	}
	public String getComment() {
		return comment;
	}
	public String getResponse() {
		return response;
	}
	public String getFilename() {
		return filename;
	}
	public void setStatus(int s) {
		status = s;
	}
	public void setResponse(String r) {
		response = r;
	}
	
	public String toString() {
		String out = "";
		out="Request #" + id + "\r\n" + user + "\r\n" + request + "\r\nStatus: ";
		switch(status) {
		case 0:
			out += "Unseen";
			break;
			
		case 1:
			out += "In queue";
			break;
			
		case 2:
			out += "Declined";
			break;
			
		case 3:
			out += "Played";
			break;
			
		default:
			out += "Indeterminate";
			break;
		}
		out += "\r\nComment: " + comment + "\r\nResponse: " + response;
		return out;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Request) && (this.id == ((Request)o).id);
	}
}
