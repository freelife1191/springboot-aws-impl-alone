package com.study.book.springboot.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by KMS on 2021/01/01.
 */
public class ProfileControllerUnitTest {
    
    @Test
    public void real_profile이_조회된다() {
        //given
        String expectedProfile = "real";
        // 스프링에서 제공하는 가짜 구현 체인
        MockEnvironment env = new MockEnvironment();
        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("oauth");
        env.addActiveProfile("real-db");
        
        ProfileController controller = new ProfileController(env);
        
        //when
        String profile = controller.profile();
        
        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }
    
    @Test
    public void real_profile이_없으면_첫번째가_조회된다() {
        //given
        String expectedProfile = "oauth";
        MockEnvironment env = new MockEnvironment();
        
        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("real-db");
        
        ProfileController controller = new ProfileController(env);
        
        //when
        String profile = controller.profile();
        
        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }
    
    @Test
    public void active_profile이_없으면_default가_조회된다() {
        //given
        String expectedProfile = "default";
        MockEnvironment env = new MockEnvironment();
        ProfileController controller = new ProfileController(env);
        
        //when
        String profile = controller.profile();
        
        //then
        assertThat(profile).isEqualTo(expectedProfile);
    }
}
