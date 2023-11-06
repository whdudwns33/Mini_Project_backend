package com.book.gpt.dao;

import com.book.gpt.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BookDTO> findAllBooks() {
        String sql = "SELECT * FROM BOOK";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new BookDTO(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("genre"),
                        rs.getString("image_url"),
                        rs.getString("content_url"),
                        rs.getString("summary"),
                        rs.getInt("price"),
                        rs.getDate("publish_year"),
                        rs.getDate("entry_time"),
                        rs.getInt("purchase_count")
                ));
    }
}
