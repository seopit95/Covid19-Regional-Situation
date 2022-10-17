package covid19;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sound.sampled.ReverbType;

public class DBConnection {
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet rs = null;
	private String inoculation;
	
	// connection
	public void connect() {
		Properties properties = new Properties();
		
		try{
			FileInputStream fis = new FileInputStream("C:\\thisisjava\\covidproject\\src\\covid19\\db.properties");
			properties.load(fis);
		}catch(FileNotFoundException e) {
			System.out.println("FileInputStream Error" + e.getMessage());
		}catch(IOException e) {
			System.out.println("Properties Error"  + e.getMessage());
		}
		
		try {
			//드라이브 코드
			Class.forName(properties.getProperty("driver"));
			//데이터베이스 접속요청
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("userid")
					, properties.getProperty("password"));
		}catch(ClassNotFoundException e) {
			System.out.println("Class.forName Error" + e.getMessage());
		}catch(SQLException e) {
			System.out.println("Connection Error" + e.getMessage());
		}
	}

	// insert statement
	public int insert(Covid covid) {
		PreparedStatement ps = null;
		int insertReturnValue = -1;
		
		String insertQuery = "call procedure_insert_covid (?,?,?)";
		
		try {
			ps = connection.prepareStatement(insertQuery);
			ps.setString(1,covid.getRegion());
			ps.setInt(2, covid.getConfirmed());
			ps.setInt(3, covid.getDeath());
			insertReturnValue = ps.executeUpdate();
		}catch(SQLException e) {
			System.out.println("InsertReturnValue Error" + e.getMessage() + e.getErrorCode());
		}catch(Exception e){
			System.out.println("Error" + e.getMessage());
		}finally {
			try {
				if(ps != null) {
					ps.close();
				}
			}catch(SQLException e) {
				System.out.println("PreparedStatemnet Error" + e.getMessage());
			}
		}
		return insertReturnValue;
	}
	
	// select statement 
	public List<Covid> select() {
		List<Covid> list = new ArrayList<Covid>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectQuery = "select * from covid";
		try {
			ps = connection.prepareStatement(selectQuery);
			rs = ps.executeQuery(selectQuery);
			//결과값 없을 경우를 체크
			if(!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			
			while(rs.next()) {
				String region = rs.getString("region");
				int confirmed = rs.getInt("confirmed");
				int death = rs.getInt("death");
				double conPercentage  = rs.getDouble("conPercentage");
				double deathPercentage = rs.getDouble("deathPercentage");
				String rating = rs.getString("rating");
				int ranking = rs.getInt("ranking");
				list.add(new Covid(region, confirmed, death, conPercentage, deathPercentage, rating, ranking));
			}
		}catch(Exception e) {
			System.out.println("Select Error" + e.getMessage());
		}finally {
			try {
				if(ps != null) {
					ps.close();
				}
			}catch(SQLException e) {
				System.out.println("PreparedStatement Error" + e.getMessage());
			}
		}
		return list;
	}
	
	// selectSearch statement
	public List<Covid> selectSearch(String data) {
		List<Covid> list = new ArrayList<Covid>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectSearchQuery = "select * from covid where region like ?";
		try {
			ps = connection.prepareStatement(selectSearchQuery);
			ps.setString(1, "%" + data + "%");
			rs = ps.executeQuery();
			
			if(!(rs != null || rs.isBeforeFirst())){
				return list;
			}
			
			while(rs.next()) {
				String region = rs.getString("region");
				int confirmed = rs.getInt("confirmed");
				int death = rs.getInt("death");
				double conPercentage  = rs.getDouble("conPercentage");
				double deathPercentage = rs.getDouble("deathPercentage");
				String rating = rs.getString("rating");
				int ranking = rs.getInt("ranking");
				list.add(new Covid(region, confirmed, death, conPercentage, deathPercentage, rating, ranking));
			}
		}catch(Exception e) {
			System.out.println("SelectSearch Error" + e.getMessage());
		}finally {
			try {
				if(ps != null) {
					ps.close();
				}
			}catch(SQLException e) {
				System.out.println("PreparedStatement Error" + e.getErrorCode());
			}
		}
		return list;
	}
	
	// Update statement
	public int update(Covid covid) {
		PreparedStatement ps = null;
		int UpdateReturnValue = -1;
		String updateQuery = "call procedure_update_covid(?,?,?)";

		try {
			ps = connection.prepareStatement(updateQuery);
			ps.setString(1, covid.getRegion());
			ps.setInt(2, covid.getConfirmed());
			ps.setInt(3, covid.getDeath());
			
			UpdateReturnValue = ps.executeUpdate();
		}catch(Exception e) {
			System.out.println("Update Error" + e.getMessage());
		}finally {
			try {
				if(ps != null) {
					ps.close();
				}
			}catch(SQLException e) {
				System.out.println("PreparedStatement Error" + e.getMessage());
			}
		}
		return UpdateReturnValue;
	}
	
	// Delete Statement
	public int delete(String region) {
		PreparedStatement ps = null;
		int deleteReturnValue = -1;
		String deleteQuery = "delete from covid where region = ? ";
		try {
			ps = connection.prepareStatement(deleteQuery);
			ps.setString(1, region);
			deleteReturnValue = ps.executeUpdate();
		}catch(Exception e) {
			System.out.println("Delete Error! " + e.getMessage());
		}finally {
			try {
				if(ps != null) {
					ps.close();
				}
			}catch(SQLException e){
				System.out.println("PreparedStatement Error " + e.getMessage());
			}
		}
		return deleteReturnValue;
	}
	
	// SelectOrderBy Statement
	public List<Covid> seletOrderBy(int type) {
		final int CONFIRMED = 1, DEATH = 2;
		List<Covid> list = new ArrayList<Covid>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectOrderByQuery = "select * from covid order by ";
		try {
			switch(type){
				case CONFIRMED:
					selectOrderByQuery += "confirmed desc";
					break;
				case DEATH:
					selectOrderByQuery += "death asc";
					break;
				default :
					System.out.println("정렬 타입 오류");
					return list;
			}
			
			ps = connection.prepareStatement(selectOrderByQuery);
			rs = ps.executeQuery(selectOrderByQuery);
			if(!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			
			int rank = 0;
			while(rs.next()) {
				String region = rs.getString("region");
				int confirmed = rs.getInt("confirmed");
				int death = rs.getInt("death");
				double conPercentage  = rs.getDouble("conPercentage");
				double deathPercentage = rs.getDouble("deathPercentage");
				String rating = rs.getString("rating");
				int ranking = rs.getInt("ranking");
				ranking = ++rank;
				list.add(new Covid(region, confirmed, death, conPercentage, deathPercentage, rating, ranking));
			}
		}catch(Exception e) {
			System.out.println("OrderBy Error. " + e.getMessage());
		}finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}
	
	// selectMaxMin Statement
	public List<Covid> selectMaxMin(int type) {
		final int conPercentage_MAX = 1, conPercentage_MIN = 2;
		List<Covid> list = new ArrayList<Covid>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String selectMaxMinQuery = "select * from covid where conPercentage = ";
		try {
			switch(type){
				case conPercentage_MAX:
					selectMaxMinQuery += "(select max(conPercentage) from covid)";
					break;
				case conPercentage_MIN:
					selectMaxMinQuery += "(select min(conPercentage) from covid)";
					break;
				default :
					System.out.println("통계 타입 오류");
					return list;
			}
			ps = connection.prepareStatement(selectMaxMinQuery);
			// success -> return 1
			rs = ps.executeQuery(selectMaxMinQuery);

			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			int rank = 1;
			while(rs.next()) {
				String region = rs.getString("region");
				int confirmed = rs.getInt("confirmed");
				int death = rs.getInt("death");
				double conPercentage  = rs.getDouble("conPercentage");
				double deathPercentage = rs.getDouble("deathPercentage");
				String rating = rs.getString("rating");
				int ranking = rs.getInt("ranking");
				if(type == 1) {
					ranking = rank;
				}
				list.add(new Covid(region, confirmed, death, conPercentage, deathPercentage, rating, ranking));
			}
		}catch(Exception e) {
			System.out.println("SelectStats Error. " +e.getMessage() );
		}finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement close error " + e.getMessage());
			}
		}
		return list;
	}	
	
	//Connect close
	public void close() {
		try {
			if(connection != null) {
				connection.close();
			}
		}catch(SQLException e) {
			System.out.println("Statemedn or ResultSet Error" + e.getMessage());
		}
	}


}
	