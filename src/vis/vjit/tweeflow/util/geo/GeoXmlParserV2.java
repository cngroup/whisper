package vis.vjit.tweeflow.util.geo;

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
public class GeoXmlParserV2 extends DefaultHandler {

	private static final String CountryName = "CountryName";

	private static final String AdministrativeAreaName = "AdministrativeAreaName";

	private static final String LocalityName = "LocalityName";

	private static final String LatLonBox = "LatLonBox";

	private static final String coordinates = "coordinates";

	private static final String Placemark = "Placemark";

	private boolean isP1 = false;

	private boolean isData = false;

	private StringBuffer m_buff = null;

	private GeoInfoV3 m_info = null;

	public GeoXmlParserV2() {
		m_buff = new StringBuffer();
	}

	public GeoInfoV3 getGeoInfo() {
		return m_info;
	}
	
	public void reset() {
		m_info = null;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		if (qName.equals(Placemark)) {
			String id = attr.getValue("id");
			if ("p1".equals(id)) {
				m_info = new GeoInfoV3();
				isP1 = true;
			}
		} else if (qName.equals(CountryName)) {
			if (isP1) {
				isData = true;
			}
		} else if (qName.equals(AdministrativeAreaName)) {
			if (isP1) {
				isData = true;
			}
		} else if (qName.equals(LocalityName)) {
			if (isP1) {
				isData = true;
			}
		} else if (qName.equals(LatLonBox)) {
			if (isP1) {
				m_info.north = Double.parseDouble(attr.getValue("north"));
				m_info.south = Double.parseDouble(attr.getValue("south"));
				m_info.east = Double.parseDouble(attr.getValue("east"));
				m_info.west = Double.parseDouble(attr.getValue("west"));
			}
		} else if (qName.equals(coordinates)) {
			if (isP1) {
				isData = true;
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
		if (qName.equals(Placemark)) {
			if(isP1) isP1 = false;
		} else if (qName.equals(CountryName)) {
			if (isP1) {
				isData = false;
				m_info.country = m_buff.toString().trim();
				m_buff.delete(0, m_buff.length());
			}
		} else if (qName.equals(AdministrativeAreaName)) {
			if (isP1) {
				isData = false;
				m_info.state = m_buff.toString().trim();
				if("".equals(m_info.state)) {
					m_info.state = "Unknown";
				}
				m_buff.delete(0, m_buff.length());
			}
		} else if (qName.equals(LocalityName)) {
			if (isP1) {
				isData = false;
				m_info.city = m_buff.toString().trim();
				if("".equals(m_info.city)) {
					m_info.city = "Unknown";
				}
				m_buff.delete(0, m_buff.length());
			}
		} else if (qName.equals(coordinates)) {
			if (isP1) {
				isData = false;
				String coord = m_buff.toString();
				String[] lalo = coord.split("\\s*\\,\\s*");
				if (lalo != null && lalo.length == 2) {
					m_info.logitude = Double.parseDouble(lalo[0]);
					m_info.latitude = Double.parseDouble(lalo[1]);
				}
				m_buff.delete(0, m_buff.length());
			}
		}
	}
}