package eu.trentorise.smartcampus.domain.fb.events.converter;

import java.io.Serializable;

import eu.trentorise.smartcampus.domain.discovertrento.GenericEvent;
import eu.trentorise.smartcampus.domain.discovertrento.GenericPOI;

public class FBEvent implements Serializable {

	private GenericEvent event;
	private GenericPOI poi;
	
	
	public GenericEvent getEvent() {
		return event;
	}
	public void setEvent(GenericEvent event) {
		this.event = event;
	}
	public GenericPOI getPoi() {
		return poi;
	}
	public void setPoi(GenericPOI poi) {
		this.poi = poi;
	}
	
	

}
