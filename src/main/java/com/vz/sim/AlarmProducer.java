package com.vz.sim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.websocket.Session;

public class AlarmProducer implements Runnable {
    
    private static AlarmProducer instance;
    private static Map<String, Session> sMap = new HashMap<String, Session>();
    ArrayList<String> alarms = new ArrayList<String>();
    private AlarmProducer(String type) {
    	readAllaAlarms(type);
    }
    
    private void readAllaAlarms(String type) {
    	try {
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
					alarms.add(json.toString());
					json.delete(0, json.length());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void add(Session s) {
        sMap.put(s.getId(), s);
    }
    
    public static void initialize(String type) {
        if (instance == null) {
            instance = new AlarmProducer(type);
            new Thread(instance).start();
        }
    }
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
                for (String key : sMap.keySet()) {
                    Session s = sMap.get(key); 
                    if (s.isOpen()) {
                    	alarms.stream().forEach( alarm -> {
                            try {
								s.getBasicRemote().sendText(alarm);
							} catch (IOException e) {
								e.printStackTrace();
							}
                    	});
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