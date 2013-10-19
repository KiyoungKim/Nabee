package com.nabsys.nabeeplus.views.model;

public class NBEvent {
	protected Object actedUpon;
	
	public NBEvent(Object receiver) {
		actedUpon = receiver;
	}
	
	public Object receiver() {
		return actedUpon;
	}
}
