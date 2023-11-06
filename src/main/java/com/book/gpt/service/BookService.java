package com.book.gpt.service;

import com.book.gpt.dao.BookDAO;
import com.book.gpt.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookDAO bookDAO;

    public List<BookDTO> getAllBooks() {
        return bookDAO.findAllBooks();
    }

    // 새로운 책을 데이터베이스에 추가하는 메소드
    public BookDTO addBook(BookDTO newBook) {
        return bookDAO.save(newBook);
    }

    // 책을 데이터베이스에서 삭제하는 메소드
    public void deleteBook(int id) {
        bookDAO.deleteById(id);
    }
}

