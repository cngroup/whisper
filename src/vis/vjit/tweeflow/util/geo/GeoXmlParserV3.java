package vis.vjit.tweeflow.util.geo;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/***
 * 
 * This piece of code is a joint research with Harvard University. 
 * It is based on the CPL opensource license. Please check the 
 * term before using.
 * 
 * The paper is published in InfoVIs 2013: 
 * "Whisper: Tracing the Spatiotemporal Process of Information Diffusion in Real Time"
 * 
 * Visit Whisper's main website here : whipserseer.com
 * 
 * @author NanCao(nancao@gmail.com)
 *
 */
public class GeoXmlParserV3 extends DefaultHandler {

	private static final String address_component = "address_component";
	private static final String geometry = "geometry";
	private static final String long_name = "long_name";
	private static final String type = "type";
	private static final String location = "location";
	private static final String bounds = "bounds";
	private static final String lat = "lat";
	private static final String lng = "lng";
	private static final String southwest = "southwest";
	private static final String northeast = "northeast";
	private static final String locality = "locality";
	private static final String status = "status";

	private boolean baddress_component = false;
	private boolean bgeometry = false;
	private boolean blocation = false;
	private boolean bbounds = false;
	private boolean bsouthwest = false;
	private boolean bnortheast = false;
	private boolean isData = false;

	private StringBuffer m_buff = null;
	private GeoInfoV3 m_info = null;
	private String sname = "";
	private String stype = "";
	
	private int m_status = 0;

	private List<GeoInfoV3> m_results = null;

	public GeoXmlParserV3() {
		m_buff = new StringBuffer();
		m_results = new ArrayList<GeoInfoV3>();
	}

	public GeoInfoV3 getGeoInfo() {
		return m_results.isEmpty() ? null : m_results.get(0);
	}
	
	public int getStatus() {
		return m_status;
	}

	public void reset() {
		m_results.clear();
		m_status = 0;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		
		if((qName.equals(status))) {
			isData = true;
		} else if (qName.equals("result")) {
			m_info = new GeoInfoV3();
			m_results.add(m_info);
		} else if (qName.equals(address_component)) {
			baddress_component = true;
		} else if (qName.equals(long_name)) {
			if (baddress_component) {
				isData = true;
			}
		} else if (qName.equals(type)) {
			if (baddress_component) {
				isData = true;
			}
		} else if (qName.equals(geometry)) {
			bgeometry = true;
		} else if (qName.equals(location)) {
			if (bgeometry) {
				blocation = true;
			}
		} else if (qName.equals(lat)) {
			if (blocation || bsouthwest || bnortheast) {
				isData = true;
			}
		} else if (qName.equals(lng)) {
			if (blocation || bsouthwest || bnortheast) {
				isData = true;
			}
		} else if (qName.equals(bounds)) {
			bbounds = true;
		} else if (qName.equals(southwest)) {
			if (bbounds) {
				bsouthwest = true;
			}
		} else if (qName.equals(northeast)) {
			if (bbounds) {
				bnortheast = true;
			}
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (isData) {
			m_buff.append(new String(ch, start, length));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals(status)) {
			isData = false;
			String s = m_buff.toString();
			if("ZERO_RESULTS".equals(s)) {
				m_status = 0;
				m_info = null;
				m_results.clear();
				System.err.println("ZERO_RESULTS");
			} else if("OVER_QUERY_LIMIT".equals(s)) {
				m_info = null;
				m_results.clear();
				m_status = -1;
				System.err.println("OVER_QUERY_LIMIT");
			} else if("REQUEST_DENIED".equals(s)) {
				m_status = 1;
				m_info = null;
				m_results.clear();
				System.err.println("REQUEST_DENIED");
			}
			m_buff.delete(0, m_buff.length());
		} else if (qName.equals("result")) {
			m_info = null;
		} else if (qName.equals(address_component)) {
			baddress_component = false;
		} else if (qName.equals(long_name)) {
			sname = m_buff.toString();
			m_buff.delete(0, m_buff.length());
			isData = false;
		} else if (qName.equals(type)) {
			stype = m_buff.toString();
			if ("country".equals(stype)) {
				m_info.country = sname;
			} else if ("administrative_area_level_1".equals(stype)) {
				m_info.state = sname;
			} else if ("administrative_area_level_2".equals(stype)) {
//				m_info.city = sname;
			} else if ("locality".equals(stype)) {
				m_info.city = sname;
			}
			m_buff.delete(0, m_buff.length());
			isData = false;
		} else if (qName.equals(geometry)) {
			bgeometry = true;
		} else if (qName.equals(location)) {
			blocation = false;
		} else if (qName.equals(lat)) {
			if (blocation) {
				m_info.latitude = Double.parseDouble(m_buff.toString());
			} else if (bsouthwest) {
				m_info.south = Double.parseDouble(m_buff.toString());
			} else if (bnortheast) {
				m_info.north = Double.parseDouble(m_buff.toString());
			}
			isData = false;
			m_buff.delete(0, m_buff.length());
		} else if (qName.equals(lng)) {
			if (blocation) {
				m_info.logitude = Double.parseDouble(m_buff.toString());
			} else if (bsouthwest) {
				m_info.west = Double.parseDouble(m_buff.toString());
			} else if (bnortheast) {
				m_info.east = Double.parseDouble(m_buff.toString());
			}
			isData = false;
			m_buff.delete(0, m_buff.length());
		} else if (qName.equals(bounds)) {
			bbounds = false;
		} else if (qName.equals(southwest)) {
			bsouthwest = false;
		} else if (qName.equals(northeast)) {
			bnortheast = false;
		}
	}
}