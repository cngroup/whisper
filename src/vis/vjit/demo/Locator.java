package vis.vjit.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import vis.vjit.tweeflow.io.TweetInfo;
import vis.vjit.tweeflow.io.TwitterProxy;
import vis.vjit.tweeflow.util.geo.GeoInfoV3;
import vis.vjit.tweeflow.util.geo.GoogleGeoLocator;

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
public class Locator {
	
	private static PreparedStatement ps_update = null;
	
	private static PreparedStatement ps_delete = null;

	public Locator() {
	}

	public static void process(String dbname) {
		try {
			ResultSet rt = TwitterProxy
					.executeQuery("select name from catelog order by time;");
			int num = 0;
			
			boolean bshutdown = false;
			while (rt.next() && !bshutdown) {
				String table = rt.getString(1);
				System.out.println("--> update table : " + table);
				ps_update = TwitterProxy
						.prepareStatement(String.format(
								"update %s set status = ? where id = ?;", table));
				ps_delete = TwitterProxy
				.prepareStatement(String.format(
						"delete from %s where id = ?;", table));
				TweetInfo tinfo = null;
				ResultSet rs = TwitterProxy
						.executeQuery(String
								.format("select * from %s where status is not null order by time;",
										table));
				while (rs.next() && !bshutdown) {
					byte[] obj = rs.getBytes("status");
					long id = rs.getLong("id");
					ObjectInputStream in = new ObjectInputStream(
							new ByteArrayInputStream(obj));
					tinfo = (TweetInfo) in.readObject();
					in.close();
					if (tinfo.geoinfo != null) {
						System.out
								.println("---->the geo location info already exists : "
										+ tinfo.geoinfo);
						continue;
					}
					
					String loc = tinfo.status.getUser().getLocation();
					GeoInfoV3 geo = GoogleGeoLocator.locateV3(loc);
					tinfo.geoinfo = geo;
					if (geo == null) {
						System.out
								.println("---->faile to locate using Google API");
						if(-1 == GoogleGeoLocator.getStatus()) {
							bshutdown = true;
							System.out.println("shutdown");
						} else {
//							ps_delete.clearParameters();
//							ps_delete.setLong(1, id);
//							ps_delete.executeUpdate();
						}
						Thread.sleep(300);
						continue;
					}
					
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(output);
					out.writeObject(tinfo);
					out.flush();
					out.close();

					System.out.println(String.format(
							"----> updating [%d | %s]", id, tinfo.geoinfo));
					ps_update.setBytes(1, output.toByteArray());
					ps_update.setLong(2, id);
					ps_update.executeUpdate();
					num++;
					if (num == 2500) {
						System.err.println("Break because of over the query limitation.");
						ps_update.executeBatch();
						ps_update.close();
						ps_update = null;
						System.exit(-1);
					}
					Thread.sleep(1000);
				}
				ps_delete.executeBatch();
				ps_delete.close();
				ps_delete = null;
				ps_update.executeBatch();
				ps_update.close();
				ps_update = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void main(String[] args) {
		try {
			if (args == null || args.length != 1) {
				System.out.println("java -jar locator.jar <dbname>");
				return;
			}
			TwitterProxy.connectDB(args[0]);
			Locator.process(args[0]);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps_update != null) {
					ps_update.executeBatch();
					ps_update.close();
				}
				
				if(ps_delete != null) {
					ps_delete.executeBatch();
					ps_delete.close();
				}
				TwitterProxy.disconnect();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
