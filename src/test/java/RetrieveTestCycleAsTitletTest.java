import com.hu.oneclick.OneClickApplication;
import com.hu.oneclick.dao.TestCycleDao;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.server.service.RetrieveTestCycleAsTitleService;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = OneClickApplication.class)
@RunWith(SpringRunner.class)
public class RetrieveTestCycleAsTitletTest {

    @Autowired
    private RetrieveTestCycleAsTitleService rtcatService;
    @Resource
    private TestCycleDao testCycleDao;

    @Test
    public void testGetIdByTitle() {
//        Resp<Long> id = rtcatService.getIdForTitle("标题221", 361971315692802048L);
        Long id = testCycleDao.getIdByTitle("标题221", 361971315692802048L);
        System.out.println("id = " + id);
    }
}
