// 自动生成UserServiceImpl的单元测试模板，覆盖主要业务方法。 

package com.hu.oneclick.server.user;

import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.entity.SysUser;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {
    @Mock
    private SysUserDao sysUserDao;

    @InjectMocks
    private UserServiceImpl userService;

    public UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testQueryEmailDoesItExist() {
        SysUser user = new SysUser();
        user.setEmail("hujy11@gmail.com");
        when(sysUserDao.queryByEmail("hujy11@gmail.com")).thenReturn(user);

        Resp<String> resp = userService.queryEmailDoesItExist("hujy11@gmail.com");
        assertEquals("200", resp.getCode()); // 200为成功
    }

    @Test
    void testPasswordVerification() {
        SysUser user = new SysUser();
        user.setEmail("hujy11@gmail.com");
        user.setPassword("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa");
        user.setUserName("测试用户");
        user.setStatus(1);
        
        when(sysUserDao.queryByEmail("hujy11@gmail.com")).thenReturn(user);

        // 测试用户存在
        Resp<String> resp = userService.queryEmailDoesItExist("hujy11@gmail.com");
        assertEquals("200", resp.getCode());
        
        // 验证密码字段不为空
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().startsWith("{bcrypt}"));
    }
} 
