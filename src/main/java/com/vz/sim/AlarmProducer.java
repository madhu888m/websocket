package com.vz.sim;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.websocket.Session;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AlarmProducer implements Runnable {

	private Map<String, Session> sMap = new HashMap<String, Session>();
	ArrayList<String> alarms = new ArrayList<String>();
	private String type = "";
	public AlarmProducer(String type, Session s) {
		this.type = type;
		sMap.put(s.getId(), s);
		readAllaAlarms();
		init();
		lookupAlarmsChange();
	}
	WatchService watcher = null;
	private void init() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Path dir = Paths.get("../alarms/"+type);
			dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void lookupAlarmsChange() {
		new Thread(new Runnable() {
			public void run() {
				lookup();
			}
			private void lookup() {
				while(true) {
					try {
						WatchKey key = watcher.take();
						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent.Kind<?> kind = event.kind();
							WatchEvent<Path> ev = (WatchEvent<Path>) event;
							Path fileName = ev.context();
							System.out.println(kind.name() + ": " + fileName);
							if (kind == StandardWatchEventKinds.OVERFLOW) {
								continue;
							} else if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_DELETE) {
								readAllaAlarms();
							}
						}
						boolean valid = key.reset();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	@SuppressWarnings("unchecked")
	private void readAllaAlarms() {
		try {
			synchronized (alarms) {
				alarms.clear();
			}
			StringBuilder json = new StringBuilder();
			Files.list(Paths.get("../alarms/"+type))
			.filter(Files::isRegularFile)
			.forEach( file -> {
				System.out.println(file);
				try(Scanner scan = new Scanner(file)){
					while(scan.hasNextLine()) {
						String line = scan.nextLine();
						json.append(line);
					}
					JSONArray jsonArray = getJsonArray(json.toString());
					synchronized (alarms){
						jsonArray.stream().forEach(alarm -> {
							String alarmString = "{   \"alarm\": "+((JSONObject)alarm).toJSONString()+ "  }";
							alarms.add(alarmString);
						});
					}
					json.delete(0, json.length());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private JSONArray getJsonArray(String jsonString) {
		JSONArray jsonArray = new JSONArray();
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(jsonString);
			JSONObject output = (JSONObject) object.get("output");
			if(output != null) {
				jsonArray = (JSONArray) output.get("alarms");
			}
			else
			{
				jsonArray.add(object.get("alarm"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000);
				for (String key : sMap.keySet()) {
					Session s = sMap.get(key); 
					if (s.isOpen()) {
						synchronized (alarms) {
							alarms.stream().forEach( alarm -> {
								try {
									s.getBasicRemote().sendText(alarm);
									Thread.sleep(3000);
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
						}
					} else {
						sMap.remove(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}