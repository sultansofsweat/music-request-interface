package com.stereodustparticles.musicrequestsystem.mri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MRSInterface {
	private HashMap<String,String> config;
	
	public MRSInterface(String url, String key, String ver) {
		config = new HashMap<>();
		config.put("URL",url);
		config.put("Key",key);
		config.put("Version",ver);
	}
	public MRSInterface() throws OneJobException {
		String url = "";
		String key = "";
		String version = "";
		String line;
		try {
			FileReader fileReader = new FileReader("config.mri");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] entry = line.split(",");
                if(entry.length == 2) {
                	switch(entry[0]) {
                	case "url":
                		url = entry[1];
                		break;
                	case "key":
                		key = entry[1];
                		break;
                	case "version":
                		version = entry[1];
                		break;
                	}
                }
            }   

            bufferedReader.close();
            
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new OneJobException("Attempt to access array index that doesn't exist: " + e.getMessage());
		}
		catch (Exception e) {
			throw new OneJobException("Can't read file: config.mri. On first launch,\nthis is normal.");
		}
		config = new HashMap<>();
		config.put("URL",url);
		config.put("Key",key);
		config.put("Version",version);
	}
	
	public void saveConfig() throws OneJobException {
		String serialized = "url," + config.get("URL") + "\nkey," + config.get("Key") + "\nver," + config.get("Version");
		try {
			FileWriter fileWriter = new FileWriter("config.mri");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(serialized);
            
            bufferedWriter.close();
		}
		catch(Exception e) {
			throw new OneJobException("Cannot save configuration: " + e);
		}
	}
	
	public void changeConfig(String u, String k, String v) {
		config.put("URL",u);
		config.put("Key",k);
		config.put("Version",v);
	}
	
	public String getConfig(String which) {
		if(config.containsKey(which)) {
			return config.get(which);
		}
		return "";
		/*if(which == true) {
			return config.get("URL");
		}
		return config.get("Key");*/
	}
	
	/* MRS INTERFACE:
	 * Test connection
	 * Read system status
	 * Change system status
	 * Read requests
	 * Queue requests
	 * Decline requests
	 * Mark requests as played
	 * 
	 * System information
	 * Advanced info
	 * Archive
	 * Delete
	 * RAC
	 * API config
	 */
	public boolean test() {
		try {
			String pdata = "key=" + config.get("Key");
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autostat.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();
	        if(conn.getResponseCode() == 200) {
	        	return true;
	        }
	        else {
	        	return false;
	        }
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean systemStatus() {
		try {
			String pdata = "key=" + config.get("Key");
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autostat.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = br.readLine()) != null) {
                switch(line) {
                case "yes":
                	return true;
                case "no":
            	default:
            		return false;
                }
            }
            br.close();
        }
        catch(Exception e) {
            //Some error occurred, bail out
            return false;
        }
		return false;
	}
	
	public int changeSystemStatus() {
		try {
			String pdata = "key=" + config.get("Key");
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autosys.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();
			return conn.getResponseCode();
		}
		catch(Exception e) {
			e.printStackTrace();
			return 404;
		}
	}
	
	public ArrayList<Request> getRequests() {
		ArrayList<Request> reqs = new ArrayList<>();
		ArrayList<String> output = new ArrayList<>();
		try {
			String pdata = "key=" + config.get("Key");
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autoreq.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = br.readLine()) != null) {
                output.add(line);
            }
            br.close();
        }
        catch(Exception e) {
            //Some error occurred, bail out
            return reqs;
        }
		for(String r : output) {
			String t = cleanOutput(r);
			String[] r3 = t.split("&");
			try {
				if(Integer.valueOf(r3[0]) > 0) {
					String[] r2 = new String[8];
					r2[0]="";
					r2[1]="";
					r2[2]="";
					r2[3]="";
					r2[4]="";
					r2[5]="";
					r2[6]="None";
					r2[7]="None";
					for(int i = 0; i < Math.min(r3.length,8); i++) {
						r2[i]=r3[i];
					}
					int broken1;
					int broken2;
					try {
						broken1 = Integer.valueOf(r2[0]);
					}
					catch(Exception e) {
						broken1 = 0;
					}
					try {
						broken2 = Integer.valueOf(r2[5]);
					}
					catch(Exception e) {
						broken2 = 0;
					}
					reqs.add(new Request(broken1,r2[1],r2[3],broken2,r2[6],r2[7],r2[4]));
				}
			}
			catch(Exception e) {
				continue;
			}
		}
		return reqs;
	}
	public int queue(int itemID,String comment) {
		try {
			String pdata = "key=" + config.get("Key") + "&post=" + Integer.toString(itemID) + "&comment=" + comment;
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autoqueue.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();
			return conn.getResponseCode();
		}
		catch(Exception e) {
			e.printStackTrace();
			return 404;
		}
	}
	
	public int decline(int itemID,String comment) {
		try {
			String pdata = "key=" + config.get("Key") + "&post=" + Integer.toString(itemID) + "&comment=" + comment;
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autodecline.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();
			return conn.getResponseCode();
		}
		catch(Exception e) {
			return 404;
		}
	}
	
	public int mark(int itemID) {
		try {
			String pdata = "key=" + config.get("Key") + "&post=" + Integer.toString(itemID);
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/automap.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();
			return conn.getResponseCode();
		}
		catch(Exception e) {
			return 404;
		}
	}
	
	public HashMap<String,String> getSystemVersion() throws OneJobException {
		String whichVersion = "";
		try {
			whichVersion = config.get("Version");
			if(whichVersion == null) {
				throw new OneJobException("Exit as version not set");
			}
		}
		catch (OneJobException e) {
			throw new OneJobException("MRS version indeterminate");
		}
		HashMap<String,String> out = new HashMap<>();
		out.put("major","0");
		out.put("minor","0");
		out.put("revision","0");
		out.put("release","System had ONE JOB!");
		if(whichVersion == "2.3") {
			out.put("identifier","SHOJ");
			out.put("beta","no");
		}
		else {
			out.put("buildcode","1970010100001");
			out.put("updates","0");
		}
		ArrayList<String> output = new ArrayList<>();
		try {
			String pdata = "key=" + config.get("Key");
			byte[] data = pdata.getBytes(StandardCharsets.UTF_8);
			URL uo = new URL(config.get("URL") + "/api/autover.php");
	        HttpURLConnection conn = (HttpURLConnection)uo.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        conn.setRequestProperty("charset","utf-8");
	        conn.setRequestProperty("Content-Length",Integer.toString(data.length));
	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	        dos.write(data);
	        dos.flush();
	        dos.close();

	        if(conn.getResponseCode() != 200) {
	        	throw new OneJobException(String.valueOf(conn.getResponseCode()));
	        }
	        
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = br.readLine()) != null) {
                output.add(line);
            }
            br.close();
            
            for(String x : output) {
            	if(x.length() > 0 && x.indexOf("=") >= 0) {
	            	String[] y = x.split("=");
	            	out.put(y[0],y[1]);
            	}
            }
            return out;
        }
        catch(Exception e) {
            //Some error occurred, bail out
            throw new OneJobException(e.getMessage());
        }
	}

	private String cleanOutput(String out) {
		String cleaned = out;
		cleaned = cleaned.replace("&amp;","and");
		cleaned = cleaned.replace("&quot;","\"");
		cleaned = cleaned.replaceAll("&#.+?;","");
		return cleaned;
	}
}
