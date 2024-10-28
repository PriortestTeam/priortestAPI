import com.hu.oneclick.OneClickApplication;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.entity.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = OneClickApplication.class)
@RunWith(SpringRunner.class)
public class ModelTest {

    @Autowired
    private SysUserDao userDao;

    @Test
    public void testSysUser() {
        SysUser sysUser = userDao.queryById("485201531491061760");
        System.out.println(sysUser);
    }

}
