package vis.vjit.tweeflow.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
public class TwitterDBSaver implements ITwitterSaver {

	private PreparedStatement prep_output = null;

	private int m_dispatch = 0;

	public TwitterDBSaver() {
	}

	public void save(String name) {
		try {
			if (name == null || "".equals(name)) {
				return;
			}
			name = name.replaceAll("\\s*\\,\\s*", "_");
			TwitterProxy.executeUpdate(String.format(
					"drop table if exists '%s';", name));
			TwitterProxy.executeUpdate(String.format(
					"create table '%s' (time integer, status blob, id integer);", name));
			TwitterProxy.executeUpdate(String.format(
					"create index if not exists time_idx on %s(time)", name));
			TwitterProxy.executeUpdate(String.format(
					"insert into catelog values(%d, '%s')",
					System.currentTimeMillis(), name));
			prep_output = TwitterProxy.prepareStatement(String.format(
					"insert into %s values(?,?,?)", name));
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	public void output(TweetInfo info) {
		try {
			if (prep_output == null) {
				System.err
						.println("please add your dataset into database first.");
				return;
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.writeObject(info);
			out.flush();
			out.close();
			
			prep_output.setLong(1, info.status.getCreatedAt().getTime());
			prep_output.setBytes(2, output.toByteArray());
			prep_output.setLong(3, info.status.getId());
			prep_output.addBatch();
			m_dispatch++;
			if (m_dispatch == 50) {
				prep_output.executeBatch();
				m_dispatch = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void flush() {
		try {
			if (prep_output != null) {
				prep_output.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (prep_output != null) {
				prep_output.close();
				prep_output = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
