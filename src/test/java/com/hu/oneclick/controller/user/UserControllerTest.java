// 自动生成UserController的单元测试模板，覆盖主要接口。 

package com.hu.oneclick.controller.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import com.hu.oneclick.server.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserController userController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testQueryUserInfo() {
        // 准备测试数据
        SysUser user = new SysUser();
        user.setEmail("hujy11@gmail.com");
        user.setPassword("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa");
        user.setUserName("测试用户");
        
        Resp<SysUser> expectedResp = new Resp.Builder<SysUser>().setData(user).ok();
        when(userService.queryUserInfo()).thenReturn(expectedResp);
        
        // 执行测试
        Resp<SysUser> result = userController.queryUserInfo();
        
        // 验证结果
        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertNotNull(result.getData());
        assertEquals("hujy11@gmail.com", result.getData().getEmail());
        assertEquals("测试用户", result.getData().getUserName());
    }
    
    @Test
    void testQueryEmailDoesItExist() {
        // 准备测试数据
        Resp<String> expectedResp = new Resp.Builder<String>().setData("exists").ok();
        when(userService.queryEmailDoesItExist("hujy11@gmail.com")).thenReturn(expectedResp);
        
        // 执行测试
        Resp<String> result = userController.queryEmailDoesItExist("hujy11@gmail.com");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertEquals("exists", result.getData());
    }
} 
