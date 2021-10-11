package com.artineer.spring_lecture_week_2.domain;

import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.awt.*;

@Getter
@Builder
@NoArgsConstructor      //파라미터가 없는 기본생성자
@AllArgsConstructor     //Builder의 필요로..
@Entity                 //jpa가 관리하는 domain임을 명시
public class Article {
    @Id     //pk 목적으로 쓰기위해
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //pk를 어떠한 원리로 만들건지
    Long id;
    String title;
    String content;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Article of(ArticleDto.ReqPost from) {
        return Article.builder()
                .title(from.getTitle())
                .content(from.getContent())
                .build();
    }

    public static Article of(ArticleDto.ReqPut from, Long id) {
        return Article.builder()
                .id(id)
                .title(from.getTitle())
                .content(from.getContent())
                .build();
    }
}
