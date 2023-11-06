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
}
