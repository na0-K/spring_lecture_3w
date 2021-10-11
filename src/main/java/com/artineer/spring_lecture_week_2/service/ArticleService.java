package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;
import com.artineer.spring_lecture_week_2.domain.ArticleRepository;
import com.artineer.spring_lecture_week_2.exception.ApiException;
import com.artineer.spring_lecture_week_2.exception.Asserts;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor        //생성자 생성
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    //생성
    public Long save(Article request) {
        return articleRepository.save(request).getId();
    }

    //조회
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article is not existed"));
    }

    //수정
    @Transactional
    public Article update(Article request) {
        Article article = this.findById(request.getId());

//        Article article = this.findById(request.getId());
//
//        Asserts.isNull(article, ApiCode.DATA_IS_NOT_FOUND, "article value is not existed");

//        if(Objects.isNull(article)) {
//            //예외를 던짐
//            throw new ApiException(ApiCode.DATA_IS_NOT_FOUND,"article value is not existed");
//        }

        //Article에 update 함수 생성
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    //삭제
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
