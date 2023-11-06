package com.book.gpt.controller;

import com.book.gpt.JWT.JwtAuthorizationFilter;
import com.book.gpt.dao.MemberDAO;
import com.book.gpt.dto.MemberDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class MemberController {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> memberLogin(@RequestBody Map<String, String> loginData) {
        String id = loginData.get("id");
        String pwd = loginData.get("password");

        MemberDAO dao = new MemberDAO();

        boolean loginResult = dao.loginCheck(id, pwd);
        System.out.println(loginResult);
        if (loginResult) {
            // 로그인 성공 시 토큰 생성
            SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

            String token = Jwts.builder()
                    .setSubject(id)
                    .signWith(key)
                    .compact();
            // 클라이언트에게 토큰 반환
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<Boolean> memberSignup(@RequestBody MemberDTO memberDTO) {
        MemberDAO dao = new MemberDAO();
        boolean regResult = false;

        // 비밀번호를 해싱해서 저장
        String plainPassword = memberDTO.getPassword();
        memberDTO.setPassword(plainPassword);

        if (dao.signupCheck(memberDTO.getId(), memberDTO.getEmail(), memberDTO.getTel())) {
            // 회원 가입을 수행
            regResult = dao.signup(memberDTO);
            System.out.println("회원가입");
        } else {
            System.out.println("중복된 아이디, 이메일, 전화 번호가 존재합니다 ");
        }

        return new ResponseEntity<>(regResult, HttpStatus.OK);

    }
    @GetMapping("/check-login")
    public ResponseEntity<String> checkLogin(HttpServletRequest request) {
        try {
            // JWTAuthorizationFilter에서 사용한 방식과 동일한 방식으로 토큰을 추출
            String token = jwtAuthorizationFilter.extractTokenFromRequest(request);
            System.out.println(token);

            if (token != null) {
                // 토큰이 유효하다면 로그인 상태
                System.out.println("로그인이 된 상태입니다..");
                return new ResponseEntity<>("User is logged in", HttpStatus.OK);
            } else {
                // 토큰이 없거나 유효하지 않다면 비로그인 상태
                System.out.println("로그인이 필요 합니다");
                return new ResponseEntity<>("User is not logged in", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 조영준
    // 회원 정보 조회
    @GetMapping("/member")
    public ResponseEntity<List<MemberDTO>> memberList(@RequestParam String id) {
        System.out.println("id : " + id);
        MemberDAO dao = new MemberDAO();
        List<MemberDTO> list = dao.memberInfo(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    // 입력 받은 이름, 아이디, 비밀번호, 이메일로 정보 조회
    @GetMapping("/checkInfo")
    public ResponseEntity<Boolean> memberCheck(@RequestParam String name,@RequestParam String id,@RequestParam String pw,@RequestParam String email) {
        MemberDAO dao = new MemberDAO();
        boolean isTrue = dao.memberCheck(name, id, pw, email);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }
    // 아이디 변경
    @PostMapping("/updateId")
    public ResponseEntity<Boolean> updateId (@RequestBody Map<String, String> updateId) {
        String currentId = updateId.get("currentId");
        String newId = updateId.get("newId");
        MemberDAO dao = new MemberDAO();
        boolean isTrue = dao.modifyId(currentId, newId);
        System.out.println(isTrue);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }
    // 비밀번호 변경
    @PostMapping("/updatePw")
    public ResponseEntity<Boolean> updatePw (@RequestBody Map<String, String> updatePw) {
        // 프론트엔드의 aixos의 키값과 동일한 이름일 것
        String currentPw = updatePw.get("currentPw");
        String newPw = updatePw.get("newPw");
        MemberDAO dao = new MemberDAO();
        boolean isTrue = dao.modifyPw(currentPw, newPw);
        System.out.println(isTrue);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }
    // 정보 삭제, 회원탈퇴
    @PostMapping("/delete")
    public ResponseEntity<Boolean> deleteMember (@RequestBody Map<String, String> del) {
        String getId = del.get("delId");
        MemberDAO dao = new MemberDAO();
        boolean isTrue = dao.deleteMember(getId);
        System.out.println("삭제여부 : " + isTrue);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }
    // 금액 충전
    @PostMapping("/charge")
    public ResponseEntity<Boolean> chargeCash (@RequestBody Map<String, Object> charge) {
        String getId = (String) charge.get("id");
        int getCash = (int) charge.get("cash");
        MemberDAO dao = new MemberDAO();
        boolean isTrue = dao.chargingCash(getId, getCash);
        System.out.println("충전 여부" + isTrue);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);
    }

}
