package com.book.gpt.dao;

import com.book.gpt.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BookDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BookDTO> findAllBooks() {
        String sql = "SELECT * FROM BOOK ORDER BY ID ASC";
        try {
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
        } catch (DataAccessException e) {
            throw new RuntimeException("책 정보를 조회하는 데 실패했습니다.", e);
        }
    }

    // ID를 기준으로 특정 책에 대한 데이터를 쿼리 2023/11/07 정벼리
    public BookDTO findBookById(int id) {
        String sql = "SELECT * FROM BOOK WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new BookDTO(
                            rs.getInt("id"), // ID 필드 추가
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
        } catch (DataAccessException e) {
            throw new RuntimeException("ID에 해당하는 책 정보를 조회하는 데 실패했습니다.", e);
        }
    }

    // 새로운 책 정보를 데이터베이스에 저장하는 메소드
    public BookDTO save(BookDTO book) {
        String sql = "INSERT INTO BOOK (title, author, publisher, genre, image_url, content_url, summary, price, publish_year, entry_time, purchase_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try{
            jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getPublisher(), book.getGenre(), book.getImageUrl(), book.getContentUrl(), book.getSummary(), book.getPrice(), book.getPublishYear(), book.getEntryTime(), book.getPurchaseCount());
            return book;
        } catch (DataAccessException e) {
            throw new RuntimeException("책을 저장하는데 실패했습니다.", e);
        }
        // 이 예제에서는 간단하게 처리하기 위해 저장 후의 책 정보를 반환하지 않았습니다.
        // 실제로는 저장 후의 책 정보를 데이터베이스에서 조회하여 반환하는 것이 좋습니다.
    }

    // 책 정보를 데이터베이스에서 삭제하는 메소드
    public void deleteById(int id) {
        String sql = "DELETE FROM BOOK WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            throw new RuntimeException("책을 삭제하는 데 실패했습니다.", e);
        }
    }

    public Optional<BookDTO> findBook(int id) {
        String sql = "SELECT * FROM book WHERE id = ?";
        try {
            BookDTO book = jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(BookDTO.class));
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<BookDTO> updateBook(int id, BookDTO bookDTO) {
        String sql = "UPDATE book SET title = ?, author = ?, publisher = ?, genre = ?, image_url = ?, content_url = ?, summary = ?, price = ?, publish_year = ? WHERE id = ?";
        jdbcTemplate.update(sql, bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getPublisher(), bookDTO.getGenre(), bookDTO.getImageUrl(), bookDTO.getContentUrl(), bookDTO.getSummary(),
                bookDTO.getPrice(), bookDTO.getPublishYear(), id);
        return findBook(id);
    }
    // 길종환
    public BookDTO getBookInfo(int bookId) {
        String sql = "SELECT * FROM Book WHERE ID = ?";

        return jdbcTemplate.queryForObject(sql, new RowMapper<BookDTO>() {
            @Override
            public BookDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new BookDTO(
                        rs.getInt("ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("PUBLISHER"),
                        rs.getString("GENRE"),
                        rs.getString("IMAGE_URL"),
                        rs.getString("CONTENT_URL"),
                        rs.getString("SUMMARY"),
                        rs.getInt("PRICE"),
                        rs.getDate("PUBLISH_YEAR"),
                        rs.getDate("ENTRY_TIME"),
                        rs.getInt("PURCHASE_COUNT")
                );
            }
        }, bookId);
    }
}
