// 自动生成UserController的单元测试模板，覆盖主要接口。 

package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.server.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testQueryUserInfo() throws Exception {
        SysUser user = new SysUser();
        user.setEmail("hujy11@gmail.com");
        user.setPassword("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa");
        user.setUserName("测试用户");
        Mockito.when(userService.queryUserInfo()).thenReturn(new Resp.Builder<SysUser>().setData(user).ok());

        mockMvc.perform(get("/user/queryUserInfo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("hujy11@gmail.com"))
                .andExpect(jsonPath("$.data.userName").value("测试用户"));
    }

    @Test
    void testQueryEmailDoesItExist() throws Exception {
        Mockito.when(userService.queryEmailDoesItExist("hujy11@gmail.com")).thenReturn(new Resp.Builder<String>().ok());

        mockMvc.perform(get("/user/queryEmailDoesItExist")
                .param("email", "hujy11@gmail.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 
