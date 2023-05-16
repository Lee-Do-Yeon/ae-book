package com.c201.aebook.api.book.persistence.entity;

import com.c201.aebook.api.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@ToString
@DynamicUpdate
@DynamicInsert
@Entity
@Getter
@NoArgsConstructor
@Table(name = "book")
public class BookEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publisher", length = 50)
    private String publisher;

    @Column(name = "publish_date")
    private Date publishDate;

    @Column(name = "isbn", nullable = false, length = 40)
    private String isbn;

    @Column(name = "page")
    private int page;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "aladin_url", nullable = false, length = 200)
    private String aladinUrl;

    @Column(name = "cover_image_url", length = 200)
//    @Column(name = "cover_image_url", length = 200, columnDefinition = "표지없음 s3 링크")
    private String coverImageUrl;

    @Column(name = "score_sum", columnDefinition = "integer default 0")
    private int scoreSum;

    @Column(name = "review_count", columnDefinition = "integer default 0")
    private int reviewCount;

    @Builder
    public BookEntity(Long id, String title, String description, String author, String publisher,
        Date publishDate, String isbn, int page, int price, String aladinUrl, String coverImageUrl, int scoreSum,
        int reviewCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.isbn = isbn;
        this.page = page;
        this.price = price;
        this.aladinUrl = aladinUrl;
        this.coverImageUrl = coverImageUrl;
        this.scoreSum = scoreSum;
        this.reviewCount = reviewCount;
    }

    public void updateScoreInfo(int score) {
        this.scoreSum += score;
        this.reviewCount++;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAladinUrl(String aladinUrl) {
        this.aladinUrl = aladinUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Builder
    public BookEntity(String title, String author) {
        this.title = title;
        this.author = author;
    }
}
