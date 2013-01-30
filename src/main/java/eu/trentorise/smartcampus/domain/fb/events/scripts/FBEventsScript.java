package eu.trentorise.smartcampus.domain.fb.events.scripts;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.smartcampus.domain.discovertrento.GenericEvent;
import eu.trentorise.smartcampus.domain.discovertrento.GenericPOI;
import eu.trentorise.smartcampus.domain.fb.events.converter.FBEvent;

public class FBEventsScript {

	public static GenericEvent[] extractEvents (FBEvent events[]) {
		List<GenericEvent> result = new ArrayList<GenericEvent>();
		for (FBEvent event: events) {
			result.add(event.getEvent());
		}
		return result.toArray(new GenericEvent[result.size()]);
	}
	
	public static GenericPOI[] extractPOIs (FBEvent events[]) {
		List<GenericPOI> result = new ArrayList<GenericPOI>();
		for (FBEvent event: events) {
			result.add(event.getPoi());
		}
		return result.toArray(new GenericPOI[result.size()]);
	}	

}
