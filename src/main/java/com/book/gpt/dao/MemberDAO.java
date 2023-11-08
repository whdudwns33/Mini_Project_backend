package com.book.gpt.dao;

import com.book.gpt.JWT.JwtAuthorizationFilter;
import com.book.gpt.common.Common;
import com.book.gpt.dto.MemberDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;


@Repository

public class MemberDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pStmt = null;


    // 조영준
    // 정보 조회
    public List<MemberDTO> memberInfo(String getId) {
        List<MemberDTO> list = new ArrayList<>();
        String sql = "SELECT NAME, EMAIL, TEL, CASH FROM MEMBER WHERE ID = ?";
        try (Connection conn = Common.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, getId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("NAME");
                    String email = rs.getString("EMAIL");
                    String tel = rs.getString("TEL");
                    int cash = rs.getInt("CASH");

                    MemberDTO dto = new MemberDTO();
                    dto.setName(name);
                    dto.setEmail(email);
                    dto.setTel(tel);
                    dto.setCash(cash);
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 이름~이메일 입력시 존재하는 지 체크
    public boolean memberCheck(String name, String id, String pw, String email) {
        boolean isChecked = false;
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT NAME, ID, PASSWORD, EMAIL FROM MEMBER WHERE NAME = '" + name + "'"
                    + " AND ID = '" + id + "'"
                    + " AND PASSWORD = '" + pw + "'"
                    + " AND EMAIL = '" + email + "'";
            rs = stmt.executeQuery(sql);
            System.out.println("회원 가입 여부 확인 Name : " + name);
            System.out.println("회원 가입 여부 확인 ID : " + id);
            System.out.println("회원 가입 여부 확인 pw : " + pw);
            System.out.println("회원 가입 여부 확인 email : " + email);
            if(rs.next()) isChecked = true;
            else isChecked = false;
            System.out.println(isChecked);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Common.close(rs);
        Common.close(stmt);
        Common.close(conn);
        return isChecked;
    }

    // 아이디 변경을 위해 중복 체크
    public boolean isIdcheck(String newId) {
        // 중복 체크
        boolean isDup = false;
        try {
            conn = Common.getConnection();
            String checkSql = "SELECT COUNT(*) AS count FROM MEMBER WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, newId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                if (count == 0) {
                    System.out.println(isDup);
                    isDup = true;
                } else {
                    System.out.println(isDup);
                    isDup = false;
                }
            }
            System.out.println(isDup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDup;
    }

    public boolean modifyId( String currentId, String newId) {
        boolean isData = false;
        try {
            conn = Common.getConnection();
            // 아이디 변경을 위한 SQL 쿼리를 작성합니다.
            String updateSql = "UPDATE MEMBER SET ID = ? WHERE ID = ?";
            if (isIdcheck(newId) == true) {
                // PreparedStatement를 생성하고 파라미터 값을 설정합니다.
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newId);
                pstmt.setString(2, currentId);

                // 업데이트 쿼리를 실행합니다.
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    isData = true;
                    System.out.println("아이디 변경 완료");
                } else {
                    isData = false;
                    System.out.println("아이디 변경 실패");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isData;
    }

    // 비밀번호 중복 체크
    public boolean isPwcheck(String newPw) {
        // 중복 체크
        boolean isDup = false;
        try {
            conn = Common.getConnection();
            String checkSql = "SELECT COUNT(*) AS count FROM MEMBER WHERE PASSWORD = ?";
            PreparedStatement pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, newPw);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                if (count == 0) {
                    System.out.println(isDup);
                    isDup = true;
                } else {
                    System.out.println(isDup);
                    isDup = false;
                }
            }
            System.out.println(isDup);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDup;
    }

    public boolean modifyPw( String currentPw, String newPw) {
        boolean isData = false;
        try {
            conn = Common.getConnection();
            // 아이디 변경을 위한 SQL 쿼리를 작성합니다.
            String updateSql = "UPDATE MEMBER SET PASSWORD = ? WHERE PASSWORD = ?";
            if (isPwcheck(newPw) == true) {
                // PreparedStatement를 생성하고 파라미터 값을 설정합니다.
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newPw);
                pstmt.setString(2, currentPw);

                // 업데이트 쿼리를 실행합니다.
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    isData = true;
                    System.out.println("비밀번호 변경 완료");
                } else {
                    isData = false;
                    System.out.println("비밀번호 변경 실패");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isData;
    }

    // 삭제
    // 문제 점: try catch 구문의 바깥쪽에 선언된 boolean isData = false;
    // try catch 구문안에서 값을 변화 시키면 변화하지 않음?
    public boolean deleteMember (String getId) {
        boolean isData = false;
        int rowsUpdated = 0;
        try {
            conn = Common.getConnection();
            String sql = "DELETE FROM MEMBER WHERE ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, getId);
            rowsUpdated = pStmt.executeUpdate(); // pStmt.executeUpdate()를 호출한 후에 리소스를 닫습니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);

        if (rowsUpdated == 1) {
            isData = true;
            System.out.println("삭제 완료: " + rowsUpdated);
        } else {
            isData = false;
            System.out.println("삭제 실패: " + rowsUpdated);
        }
        return isData;
    }

    public boolean chargingCash (String getId, int getCash) {
        boolean isData = false;
        int rowsUpdated = 0;
        try {
            conn = Common.getConnection();
            String sql = "UPDATE MEMBER SET CASH = CASH + ? WHERE id = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, getCash);
            pStmt.setString(2, getId);
            rowsUpdated = pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);
        if (rowsUpdated == 1) {
            isData = true;
            System.out.println("충전 완료: " + rowsUpdated);
        } else {
            isData = false;
            System.out.println("충전 실패: " + rowsUpdated);
        }
        return isData;
    }


}