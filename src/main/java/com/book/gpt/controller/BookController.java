package com.book.gpt.controller;

import com.book.gpt.dto.BookDTO;
import com.book.gpt.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 모든 도메인에 대해 CORS 허용
// CrossOrigin이 존재하지 않으면, 리액트가 스프링 부트에 접근이 불가, 이를 허용해주는 것
@CrossOrigin("*")
@RestController // 이 클래스가 RESTful 웹 서비스의 컨트롤러 역할을 하도록 지정
@RequestMapping("/book")

public class BookController {

    @Autowired // BookService를 이 클래스에 주입합니다.
    private BookService bookService;

    // ID로 책 조회 2023/11/07 정벼리
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable int id) {
        try {
            BookDTO book = bookService.getBookByID(id);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
           // 데이터가 조회되지 않았을때 발생하는 에러를 처리하기 위한 예외처리
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/admin")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping("/admin")  // '/book' URL로 POST 요청이 오면 이 메소드를 실행합니다.
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO newBook) {
        // 요청 본문에 포함된 책 정보를 데이터베이스에 추가합니다.
        BookDTO book = bookService.addBook(newBook);

        // 책 정보가 성공적으로 추가되면, 추가된 책 정보와 함께 HTTP 상태 코드 201(Created)를 응답으로 반환합니다.
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}


