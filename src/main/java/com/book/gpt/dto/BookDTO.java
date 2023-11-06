package com.book.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class BookDTO {
    private int bookNo;
    private String title;
    private String author;
    private String publisher;
    private String genre;
    private String imageUrl;
    private String contentUrl;
    private String summary;
    private double price;
    private Date publishYear;
    private Date entry;
    private int purchaseCount;

    // 조영준: 생성자가 먹지 않아서 임시로 작성
    public BookDTO(int id, String title, String author, String publisher, String genre, String imageUrl, String contentUrl, String summary, int price, Date publishYear, Date entryTime, int purchaseCount) {
    }
}
