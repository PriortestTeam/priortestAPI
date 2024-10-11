import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.junit.Test;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

public class ResourceFileTest {
    @Test
    public  void testReadFile() {
        try (InputStream is = ResourceLoader.class.getResourceAsStream("/fonts/simfang.ttf")) {
            if (is == null) {
                throw new RuntimeException("Resource not found: /fonts/simfang.ttf");
            }

            // 尝试解析字体文件
            TTFParser parser = new TTFParser();
            TrueTypeFont font = parser.parseEmbedded(is);
            System.out.println("Font parsed successfully: " + font.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
