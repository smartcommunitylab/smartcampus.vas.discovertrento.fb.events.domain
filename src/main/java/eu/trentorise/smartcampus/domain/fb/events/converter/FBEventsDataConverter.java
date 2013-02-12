package eu.trentorise.smartcampus.domain.fb.events.converter;

import it.sayservice.platform.core.domain.actions.DataConverter;
import it.sayservice.platform.core.domain.ext.Tuple;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.protobuf.ByteString;

import eu.trentorise.smartcampus.domain.discovertrento.GenericEvent;
import eu.trentorise.smartcampus.domain.discovertrento.GenericPOI;
import eu.trentorise.smartcampus.domain.discovertrento.POIData;
import eu.trentorise.smartcampus.services.fb.events.data.message.Events.Event;

public class FBEventsDataConverter implements DataConverter {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");   
	
	@Override
	public Serializable toMessage(Map<String, Object> parameters) {
		if (parameters == null)
			return null;
		return new HashMap<String, Object>(parameters);
	}
	
	@Override
	public Object fromMessage(Serializable object) {
		List<ByteString> data = (List<ByteString>) object;
		Tuple res = new Tuple();
		List<FBEvent> list = new ArrayList<FBEvent>();
		for (ByteString bs : data) {
			try {
				Event ev = Event.parseFrom(bs);
				GenericEvent ge = extractGenericEvent(ev);
				GenericPOI gp = extractGenericPOI(ev);
				FBEvent fe = new FBEvent();
				fe.setEvent(ge);
				fe.setPoi(gp);
				list.add(fe);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.put("data", list.toArray(new FBEvent[list.size()]));
		return res;
	}

	private GenericEvent extractGenericEvent(Event ev) throws ParseException {
		GenericEvent ge = new GenericEvent();
		
		if (ev.hasDescription()) {
			ge.setDescription(ev.getDescription());
		} else {
			ge.setDescription("");
		}
		
		ge.setSource(ev.getOwner());

		ge.setFromTime(ev.getStartTime());
		ge.setTiming(sdf.format(new Date(ge.getFromTime())));
		
		String s = ev.getName() + "; " + ev.getLocation() + "; " + ev.getStartTime();
		ge.setId(encode(s));
		if (ev.hasPoi()) {
			ge.setPoiId(encode(ev.getPoi().getPoiId() + "@fb-events"));
		}
		ge.setTitle(ev.getName());
		
		ge.setType(extractType(ge));
		
		return ge;
	}

	private static String extractType(GenericEvent ge) {
		String res = checkStringForType(ge.getTitle());
		if (res != null) return res;
		return checkStringForType(ge.getDescription());
	}

	private static String checkStringForType(String in) {
		if (in == null) return null;
		String s = in.toLowerCase();
		if (s.matches(".*((live)|(concert)|(band)|(tribute)|(rock)|(orchestra)|(music)).*")) 
		{
			return "Concerts";
		}
		if (s.matches(".*((party)|(festa)|(dj)).*"))
		{
			return "Parties";
		}
		if (s.matches(".*((seminar)|(presentazione)).*"))
		{
			return "Seminars";
		}
		return null;
	}

	private GenericPOI extractGenericPOI(Event ev) throws ParseException {
		GenericPOI gp = new GenericPOI();
		gp.setTitle(ev.getLocation());
		gp.setDescription("");
		gp.setSource("Facebook");
		gp.setPoiData(createPOIData(ev));
		gp.setId(encode(ev.getPoi().getPoiId() + "@fb-events"));

		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("id", ev.getId());

		try {
			gp.setCustomData(new ObjectMapper().writeValueAsString(map));
		} catch (Exception e) {}

		return gp;
	}

	private POIData createPOIData(Event ev) {
		POIData poiData = new POIData();
		poiData.setStreet(ev.getPoi().getAddress().getStreet());
		poiData.setLatitude(ev.getPoi().getCoordinate().getLatitude());
		poiData.setLongitude(ev.getPoi().getCoordinate().getLongitude());
		poiData.setCity(ev.getPoi().getAddress().getCity());
		poiData.setRegion(ev.getPoi().getAddress().getRegion());
		poiData.setCountry(ev.getPoi().getAddress().getCountry());
		poiData.setState(ev.getPoi().getAddress().getState());
		return poiData;
	}

	private static String encode(String s) {
		return new BigInteger(s.getBytes()).toString(s.length());
	}
}
