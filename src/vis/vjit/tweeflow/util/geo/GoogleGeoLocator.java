package vis.vjit.tweeflow.util.geo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import vis.vjit.demo.DataIO;
import vis.vjit.tweeflow.Config;

/***
 * 
 * This piece of code is a joint research between HKUST and Harvard University. 
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
public class GoogleGeoLocator {

	private static final String BASE_URL_V2 = "http://maps.google.com/maps/geo?hl=en&q=%s&output=xml&key="
			+ Config.GOOGLE_GEO_KEY;

	private static final String BASE_URL_V3 = "http://maps.googleapis.com/maps/api/geocode/xml?address=%s&sensor=false&language=en";

	private static GeoXmlParserV2 m_parserv2 = new GeoXmlParserV2();
	private static GeoXmlParserV3 m_parserv3 = new GeoXmlParserV3();

	private static Map<String, String> m_replace = null;
	private static Map<String, Double> m_logitude = null;
	private static Map<String, Integer> m_order = null;
	private static int status = 0;

	static {
		// m_geolocater = new
		// GeoAddressStandardizer("ABQIAAAA8Y_4JtGrg6jKMnlgzhKwchRbBPOkGG6uJo0ezifOhZmanwiFyxTcxaEnWhpe4cxoK3CdLdNjamUKfg");
		try {
			m_order = DataIO.states("./data/states.txt");
			m_logitude = DataIO.location("./data/location.txt");
			m_logitude.put("Unknown", new Double(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		m_replace = new HashMap<String, String>();
		m_replace.put("Deutschland", "Germany");
		m_replace.put("Polska", "Poland");
		m_replace.put("Espanya", "Spain");
		m_replace.put("Sverige", "Sweden");
		m_replace.put("Brasil", "Brazil");
		m_replace.put("Suomi", "Finland");
		m_replace.put("Norge", "Norway");
		m_replace.put("Danmark", "Denmark");
	}

	public GoogleGeoLocator() {
	}
	
	public static double getLogitude(String country) {
		String cc = m_replace.get(country);
		if(cc != null) {
			country = cc;
		}
		country = country.toUpperCase();
		Double dd = m_logitude.get(country);
		if(dd == null) {
//			System.err.println(country);
		}
		
		return dd == null ? -1 : dd.doubleValue();
	}
	
	public static int getOrder(String state) {
		Integer order =  m_order.get(state.toUpperCase());
		if(order == null) {
			System.err.println(state);
		}
		return order == null ? -1 : order;
	}

	public static GeoInfoV3 locateV2(String loc) {
		try {

			if ("".equals(loc) || loc == null) {
				return null;
			}
			m_parserv2.reset();
			URL url = new URL(String.format(BASE_URL_V2, loc));
			InputStream in = new BufferedInputStream(url.openStream());
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			SAXParser parser;
			parser = factory.newSAXParser();
			parser.parse(new InputSource(in), m_parserv2);
			GeoInfoV3 info = m_parserv2.getGeoInfo();
			//info.logitude = m_logitude.get(info.country);
			return info;
		} catch (MalformedURLException e) {
			// e.printStackTrace();
			return null;
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
			return null;
		}
	}
	
	public static int getStatus() {
		if(m_parserv3.getStatus() == -1) {
			return -1;
		}
		return status;
	}

	public static GeoInfoV3 locateV3(String loc) {
		try {

			if ("".equals(loc) || loc == null) {
				return null;
			}
			status = 0;
			m_parserv3.reset();
			URL url = new URL(String.format(BASE_URL_V3, loc));
			// System.err.println(url);
			InputStream in = new BufferedInputStream(url.openStream());
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			SAXParser parser;
			parser = factory.newSAXParser();
			parser.parse(new InputSource(in), m_parserv3);
			GeoInfoV3 info = m_parserv3.getGeoInfo();
			//info.logitude = m_logitude.get(info.country);
			return info;
		} catch (MalformedURLException e) {
//			e.printStackTrace();
			status = -2;
			return null;
		} catch (IOException e) {
//			e.printStackTrace();
			status = -2;
			return null;
		} catch (SAXException e) {
//			e.printStackTrace();
			status = -2;
			return null;
		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
			status = -2;
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(GoogleGeoLocator.locateV2("Harvard"));
	}
}
