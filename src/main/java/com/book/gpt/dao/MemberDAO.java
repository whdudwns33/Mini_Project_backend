package com.book.gpt.dao;

import com.book.gpt.common.Common;
import com.book.gpt.dto.MemberDTO;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository

public class MemberDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pStmt = null;

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hashedBytes.length);
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean loginCheck(String id, String pwd) {
        System.out.println(hashPassword(pwd));
        try {
            conn = Common.getConnection();
            String sql = "SELECT * FROM MEMBER WHERE ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, id);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                String sqlPwd = rs.getString("PASSWORD"); // 데이터베이스에서 해싱된 비밀번호를 가져옴
                String hashedPwd = hashPassword(pwd); // 사용자 입력 비밀번호를 해싱

                System.out.println(hashedPwd);
                if (hashPassword(sqlPwd).equals(hashedPwd)) {
                    return true; // 해싱된 비밀번호와 입력한 비밀번호가 일치하면 로그인 성공
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(pStmt);
            Common.close(conn);
        }
        return false;
    }



    // 중복값 체크 메서드
    public boolean signupCheck(String id, String email, String phone) {
        try {
            conn = Common.getConnection();
            String sql = "SELECT * FROM MEMBER WHERE ID = ? OR EMAIL = ? OR TEL = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, id);
            pStmt.setString(2, email);
            pStmt.setString(3, phone);
            rs = pStmt.executeQuery();

            if (rs.next()) {
                // 아이디, 이메일 또는 전화번호 중 하나라도 중복되는 경우
                Common.close(rs);
                Common.close(pStmt);
                Common.close(conn);
                return false;
            } else {
                // 중복되는 정보가 없는 경우
                Common.close(rs);
                Common.close(pStmt);
                Common.close(conn);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 회원가입 완료 메서드
    // 회원가입 완료 메서드
    public boolean signup(MemberDTO member) {
        try {
            conn = Common.getConnection();
            String sql = "INSERT INTO MEMBER(ID, PASSWORD, NAME, EMAIL, TEL, CASH) VALUES(?, ?, ?, ?, ?, ?)";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, member.getId());

            // 비밀번호를 해싱하여 저장
            String hashedPassword = hashPassword(member.getPassword());
            pStmt.setString(2, hashedPassword);

            pStmt.setString(3, member.getName());
            pStmt.setString(4, member.getEmail());
            pStmt.setString(5, member.getTel());
            pStmt.setInt(6, member.getCash());

            int rowsAffected = pStmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Common.close(pStmt);
            Common.close(conn);
        }
    }
    // MemberDAO 클래스에 findByUsername 메서드 추가
    public MemberDTO findId(String id) {
        try {
            conn = Common.getConnection();
            String sql = "SELECT * FROM MEMBER WHERE ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, id);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                MemberDTO member = new MemberDTO();
                member.setId(rs.getString("ID"));
                member.setPassword(rs.getString("PASSWORD"));
                member.setName(rs.getString("NAME"));
                member.setEmail(rs.getString("EMAIL"));
                member.setTel(rs.getString("TEL"));
                member.setCash(rs.getInt("CASH"));
                String role = findRoleById(id);
                member.setRole(role);
                return member;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(pStmt);
            Common.close(conn);
        }
        return null;
    }
    public String findRoleById(String id) {
        try {
            conn = Common.getConnection();
            String sql = "SELECT AUTH FROM MEMBER WHERE ID = ?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, id);
            rs = pStmt.executeQuery();
            if (rs.next()) {
                int auth = rs.getInt("AUTH"); // 사용자의 권한 정보를 가져옴
                return auth == 0 ? "USER" : "ADMIN"; // 권한 정보를 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Common.close(rs);
            Common.close(pStmt);
            Common.close(conn);
        }
        return null;
    }

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