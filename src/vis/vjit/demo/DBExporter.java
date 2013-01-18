package vis.vjit.demo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import vis.vjit.tweeflow.io.TweetInfo;
import vis.vjit.tweeflow.io.TwitterProxy;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;

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
public class DBExporter {

	public static void export(String dbname, String path) {
		try {
			TwitterProxy.connectDB(dbname);
			ResultSet rt = TwitterProxy
					.executeQuery("select name from catelog order by time");
			while(rt.next()) {
				String table = rt.getString(1);
				ObjectOutputStream m_out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path + table + ".wsp")));
				String sql = String.format("select * from %s where status is not null order by time", table);
				TweetInfo tinfo = null;
				GeoInfoV3 info = null;
				PreparedStatement input = TwitterProxy
						.prepareStatement(sql);
				ResultSet rs = input.executeQuery();
				while (rs.next()) {
					byte[] obj = rs.getBytes(2);
					ObjectInputStream in = new ObjectInputStream(
							new ByteArrayInputStream(obj));
					tinfo = (TweetInfo) in.readObject();
					in.close();
					info = tinfo.geoinfo;
					if(info != null) {
						m_out.writeObject(tinfo);
					}
				}
				m_out.flush();
				m_out.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DBExporter.export("./data/cases/earthquake-20120315.db", "./data/cases/export/");
	}
}
