package com.study.book.springboot.web;

import com.study.book.springboot.config.auth.LoginUser;
import com.study.book.springboot.config.auth.dto.SessionUser;
import com.study.book.springboot.service.posts.PostsService;
import com.study.book.springboot.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {
    
    private final PostsService postsService;
    private final HttpSession httpSession;
    
    @GetMapping("/")
    public String index(Model model,
                        @LoginUser SessionUser user // 세션정보 가져오기 어느 컨트롤러든지 @LoginUser로 세션 정보를 가져올 수 있음
        ) {
        model.addAttribute("posts", postsService.findAllDesc());
        
        // 세션에 저장된 값이 있을 때만 model에 userName으로 등록
        // 세션에 저장된 값이 없으면 model엔 아무런 값이 없는 상태이니 로그인 버튼이 보임
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
    
    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }
    
    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);
        
        return "posts-update";
    }
}