package vis.vjit.demo;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vis.vjit.tweeflow.io.TweetInfo;
import vis.vjit.tweeflow.io.TwitterProxy;

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
public class Converter {

	public Converter() {
	}

	public static void convert(String dbname) {
		try {
			TwitterProxy.connectDB(dbname);
			ResultSet rt = TwitterProxy
					.executeQuery("select name from catelog order by time");
			String table = "";
			while(rt.next()) {
				table = rt.getString(1);
				TwitterProxy.executeUpdate(String.format("alter table '%s' add id integer;", table));
				System.out.println("--> converting table : " + table);
				PreparedStatement ps_update = TwitterProxy.prepareStatement(String.format("update %s set id = ? where status = ? and time = ?", table));
				TweetInfo tinfo = null;
				ResultSet rs = TwitterProxy.executeQuery(String.format("select * from %s where status is not null order by time", table));
				while(rs.next()) {
					long time = rs.getLong(1);
					byte[] obj = rs.getBytes(2);
					ObjectInputStream in = new ObjectInputStream(
							new ByteArrayInputStream(obj));
					tinfo = (TweetInfo) in.readObject();
					System.out.println("-----> converting tweet : " + tinfo.status.getId());
					ps_update.clearParameters();
					ps_update.setLong(1, tinfo.status.getId());
					ps_update.setBytes(2, obj);
					ps_update.setLong(3, time);
					ps_update.executeUpdate();
				}
			}
			TwitterProxy.disconnect();
		} catch (Exception e) {
			try {
				TwitterProxy.disconnect();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			System.out.println("The database is already in the latest format");
		}
	}
	
	public static void main(String[] args) {
		if(args == null || args.length != 1) {
			System.out.println("Usage: java -jar converter.jar <dbname>");
			return;
		}
		Converter.convert(args[0]);
	}
}
