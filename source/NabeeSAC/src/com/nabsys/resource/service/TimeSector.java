package com.nabsys.resource.service;

public enum TimeSector {
	MIN,
	HUR,
	DAY,
	MON,
	YER;
	
	public static TimeSector getTimeSector(String value){
		TimeSector result = null;
		for(TimeSector type : TimeSector.values()){
			if(type.toString().equals(value)){
				result = type;
				break;
			}
		}
		return result;
	}
}
