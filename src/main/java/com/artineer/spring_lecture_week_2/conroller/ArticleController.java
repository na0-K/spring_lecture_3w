package com.artineer.spring_lecture_week_2.conroller;

import com.artineer.spring_lecture_week_2.domain.Article;
import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.exception.ApiException;
import com.artineer.spring_lecture_week_2.service.ArticleService;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
        return Response.ok(articleService.save(Article.of(request)));

//        Article article = Article.builder()
//                .title(request.getTitle())
//                .content(request.getContent())
//                .build();
//
//        Long id = articleService.save(article);

//        return Response.<Long>builder()
//                .code(ApiCode.SUCCESS)
//                .data(id)
//                .build();
    }

    @GetMapping("/{id}")
    public Response<ArticleDto.Res> get(@PathVariable Long id) {
        return Response.ok(ArticleDto.Res.of(articleService.findById(id)));

//        ArticleDto.Res response = ArticleDto.Res.builder()
//                .id(String.valueOf(article.getId()))
//                .title(article.getTitle())
//                .content(article.getContent())
//                .build();

//        return Response.<ArticleDto.Res>builder()
//                .code(ApiCode.SUCCESS)
//                .data(response)
//                .build();
    }

    @PutMapping("/{id}")
    public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
        return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));

//        try {
//            return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
//        } catch (ApiException e) {
//            return Response.builder().code(e.getCode()).data(e.getMessage()).build();
//        }

//        //요청으로 들어온 dto를 domain을 바꾸고
//        Article article = Article.builder()
//                .id(id)
//                .title(request.getTitle())
//                .content(request.getContent())
//                .build();
//
//        //domain을 활용하여 업데이트
//        Article articleResponse = articleService.update(article);
//
//        //업데이트된 내용을 다시 응담에 대한 dto롤 변경
//        ArticleDto.Res response = ArticleDto.Res.builder()
//                .id(String.valueOf(articleResponse.getId()))
//                .title(articleResponse.getTitle())
//                .content(articleResponse.getContent())
//                .build();

//        return Response.<ArticleDto.Res>builder().code(ApiCode.SUCCESS).data(response).build();
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Response.ok();
//        return Response.<Void>builder().code(ApiCode.SUCCESS).build();
    }
}
