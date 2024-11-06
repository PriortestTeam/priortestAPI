package com.hu.oneclick.common.util;

import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class PDFTableUtil {
    private float xPos = 50;
    private float yPos = 830;
    private float xLine = 380;
    private float yCur = 0;
    private float cellWidth = 190, cellHeight = 20;
    private PDDocument document;
    private PDPage page;
    private PDPageContentStream contentStream;
    private PDType0Font font;
    private int pageCount = 0;
    private String dirPath;
    private int fontSize = 10;

    public PDFTableUtil(String dirPath) throws Exception {
        this.dirPath = dirPath;
        document = new PDDocument();
        // Load the TrueType font from resources
        try (InputStream is = ResourceLoader.class.getResourceAsStream("/fonts/simfang.ttf")) {
            if (is == null) {
                throw new IOException("Font file not found: /fonts/simfang.ttf");
            }
            TTFParser parser = new TTFParser();
            TrueTypeFont ttf = parser.parseEmbedded(is);
            font = PDType0Font.load(document, ttf, true);
        } catch (IOException e) {
            throw new IOException("Error loading font: " + e.getMessage(), e);
        }
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(font, fontSize);
        pageCount++;
    }

    public void generate(String[][] datas) throws IOException {
        setYHeight(5);
        int curRow = 0;
        while (curRow < datas.length) {
            int endRow = Math.min((curRow + (int) (yPos / cellHeight)), datas.length);
            for (int i = 0; i <= (endRow - curRow); i++) {
                float rowY = yPos - (cellHeight * i);
                contentStream.moveTo(xPos, rowY);
                contentStream.lineTo(xLine, rowY);
                contentStream.stroke();
                yCur = rowY;
            }
            for (int i = 0; i < 3; i++) {
                float xPoint = i == 0 ? 50 : (cellWidth + (xPos / 2)) * i;
                xPoint = i == 2 ? xPoint - xPos : xPoint;
                contentStream.moveTo(xPoint, yPos);
                contentStream.lineTo(xPoint, yCur);
                contentStream.stroke();
            }
            int inc = 0;
            for (int i = curRow; i < endRow; i++) {
                for (int j = 0; j < datas[i].length; j++) {
                    float xText = (cellWidth * (j + 1)) / 2 + 50, yText = yPos - ((cellHeight * (inc + 1)) - 5);
                    xText = j == 0 ? xText - 50 : xText;
                    if (!datas[i][j].isEmpty() && hasEndWith(datas[i][j])) {
                        PDImageXObject image = PDImageXObject.createFromFile(datas[i][j], document);
                        contentStream.drawImage(image, (cellWidth * j) + 50, yPos - (cellHeight * i) - 20, 120, 20);
                    } else {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(xText, yText);
                        contentStream.showText(datas[i][j]);
                        contentStream.endText();
                    }
                }
                inc++;
                curRow++;
            }
            yPos = yCur;
            if (curRow < datas.length) {
                makePage();
            }
        }
    }

    public void showText(String text) throws IOException {
        setYHeight(20);
        makePage();
        contentStream.beginText();
        contentStream.newLineAtOffset(xPos, yPos);
        contentStream.showText(text);
        contentStream.endText();

    }

    public void save(String name) throws IOException {
        contentStream.close();
        document.save(dirPath + "/" + name);
        document.close();
    }

    private void setYHeight(float height) {
        if (yPos < 800 || pageCount > 1) {
            yPos = yCur -= height;
        }
    }

    private void makePage() throws IOException {
        if (yPos <= 10) {
            contentStream.close();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(font, 10);
            yPos = 800;
            yCur = 800;
            pageCount++;
        }
    }

    private boolean hasEndWith(String path) {
        Set<String> suffix = new HashSet<>();
        suffix.add(".jpg");
        suffix.add(".png");
        suffix.add(".gif");
        suffix.add(".jpeg");

        for (String s : suffix) {
            if (path.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
