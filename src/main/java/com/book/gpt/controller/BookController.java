package com.book.gpt.controller;

import com.book.gpt.dto.BookDTO;
import com.book.gpt.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
// 모든 도메인에 대해 CORS 허용
// CrossOrigin이 존재하지 않으면, 리액트가 스프링 부트에 접근이 불가, 이를 허용해주는 것
@CrossOrigin("*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }
}


