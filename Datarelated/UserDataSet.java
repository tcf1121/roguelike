package Datarelated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataSet {
    private Connection conn;
    
	public UserDataSet() {
        try {
        	//JDBC Driver 등록
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            //연결하기
            String connectionUrl = 
            		"jdbc:sqlserver://localhost:1433;"
            				+"database=rogue;"
            				+"encrypt=false;"
            				+"user=tcf12;"
            				+"password=wlslaks!12;";
            conn = DriverManager.getConnection(connectionUrl);
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	// 회원 추가
	public void addUsers(User user) {
    	try {
		String sql = ""+
				"insert into userinfo(id, pw, nickname)" +
				"values (?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, user.getId());
		pstmt.setString(2, user.getPw());
		pstmt.setString(3, user.getNickName());
		pstmt.executeUpdate();
		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	}
	// 아이디 중복 확인
    public boolean isIdOverlap(String id) {
    	String sql = "select id from userinfo";
    	try {
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			if(rs.getString("id").equals(id)) {
    				pstmt.close();
    				return true;
    			}
    		}
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    // 회원 삭제
	public void withdraw(String id) {
       try {
    	   String sql = "delete from userinfo where id=?";
    	   PreparedStatement pstmt = conn.prepareStatement(sql);
    	   pstmt.setString(1, id);
    	   pstmt.executeUpdate();
    	   pstmt.close();   	   
       }catch (SQLException e) {
    	   e.printStackTrace();
       }
    }
	// 유저 정보 가져오기
	public User getUser(String id) {
		User user1 = new User(id);
    	try {
    		String sql = ""+
				"select * from userinfo where id = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, id);
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			user1.setId(rs.getString("id"));
    			user1.setPw(rs.getString("pw"));
    			user1.setNickName(rs.getString("nickname"));
    		}
    		rs.close();
    		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		return user1;
	}
	public void UserLvUp(String id) {
		int now_exp=0, demand_exp=0, now_Lv=30;
    	try {
    		String sql = ""+
				"select Lv, ex_p, demand \n"
				+ "from userinfo join ExperienceP\n"
				+ "on userinfo.user_lv = ExperienceP.Lv\n"
				+ "where id = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, id);
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()) {
    			now_Lv = rs.getInt("Lv");
    			now_exp = rs.getInt("ex_p");
    			demand_exp = rs.getInt("demand");
    		}
    		rs.close();
    		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	if(now_Lv <30) {
    		if(now_exp >= demand_exp) {
        		try {
            		String sql = "update userinfo set user_lv +=1\n"
            				+ "where id = ?";
            		PreparedStatement pstmt = conn.prepareStatement(sql);
            		pstmt.setString(1, id);
            		pstmt.executeUpdate();
            		pstmt.close();
            	}catch(Exception e) {
            		e.printStackTrace();
            	}
        	}
    	}
    	
	}
	public void exp_up(String id, int get_exp) {
    	try {
    		String sql = "update userinfo\n"
    				+ "set ex_p += ?\n"
    				+ "where id = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setInt(1, get_exp);
    		pstmt.setString(2, id);
    		pstmt.executeUpdate();
    		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	}
	public int[] see_exp(String id) {
		int[] exp = new int[2];
    	try {
    		String sql = "select ex_p, befo_demand, accumulate \n"
    				+ "from userinfo join ExperienceP\n"
    				+ "on userinfo.user_lv = ExperienceP.Lv\n"
    				+ "where id = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, id);
    		ResultSet rs = pstmt.executeQuery();
    		while(rs.next()) {
    			exp[0] = rs.getInt("ex_p")- rs.getInt("befo_demand");
    			exp[1] = rs.getInt("accumulate");
    		}
    		rs.close();
    		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return exp;
	}
	public int now_Lv(String id) {
		int lv=0;
		try {
    		String sql = "select user_lv \n"
    				+ "from userinfo\n"
    				+ "where id = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, id);
    		ResultSet rs = pstmt.executeQuery();
    		while(rs.next()) {
    			lv = rs.getInt("user_lv");
    		}
    		rs.close();
    		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		return lv;
	}
	public int get_exp(String id) {
		int exp=0;
		try {
    		String sql = "select ex_p ,befo_demand\n"
    				+ "from userinfo join ExperienceP\n"
    				+ "on userinfo.user_lv = ExperienceP.Lv\n"
    				+ "where id = ?";
    		PreparedStatement pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, id);
    		ResultSet rs = pstmt.executeQuery();
    		while(rs.next()) {
    			exp = rs.getInt("ex_p")- rs.getInt("befo_demand");
    		}
    		rs.close();
    		pstmt.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		return exp;
	}
}