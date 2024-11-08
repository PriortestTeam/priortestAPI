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
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class PDFTableUtil {
    private float xPos = 50;
    private float yPos = 830;
    private float yCur = yPos;
    private float xLine = 380;
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
                if (i != 0) {
                    if (i == 5 && datas[4][0].equals("在线报表")) {
                        yCur = yCur - (cellHeight + (fontSize * (datas[4][1].split(",").length)));
                    } else {
                        yCur = yCur - cellHeight;
                    }
                }
                contentStream.moveTo(xPos, yCur);
                contentStream.lineTo(xLine, yCur);
                contentStream.stroke();
            }
            for (int i = 0; i < 3; i++) {
                float xPoint = i == 0 ? 50 : (cellWidth + (xPos / 2)) * i;
                xPoint = i == 2 ? xPoint - xPos : xPoint;
                contentStream.moveTo(xPoint, yPos);
                contentStream.lineTo(xPoint, yCur);
                contentStream.stroke();
            }
            for (int i = curRow; i < endRow; i++) {
                yPos = yPos - (cellHeight - 10);
                for (int j = 0; j < 2; j++) {
                    float textX = (cellWidth * (j + 1)) / 2 + 50;
                    textX = j == 0 ? textX - 50 : textX;
                    if (j == 1 && !datas[i][j].isEmpty() && hasEndWith(datas[i][j])) {
                        PDImageXObject image = PDImageXObject.createFromFile(datas[i][j], document);
                        contentStream.drawImage(image, (cellWidth * j) + 50, yPos - 10, 120, 20);
                    } else {
                        if (i == 4 && j == 1 && datas[4][0].equals("在线报表")) {
                            for (var url : datas[4][1].split(",")) {
                                contentStream.beginText();
                                contentStream.newLineAtOffset(textX, yPos);
                                contentStream.showText(url);
                                contentStream.endText();
                                yPos = yPos - fontSize;
                            }
                        } else {
                            contentStream.beginText();
                            contentStream.newLineAtOffset(textX, yPos);
                            contentStream.showText(datas[i][j]);
                            contentStream.endText();
                        }
                    }
                }
                curRow++;
                yPos = yPos - 10;
            }
            yPos = yCur;
            if (curRow < datas.length) {
                makePage();
            }
        }
    }

    public void showText(String text) throws IOException {
        setYHeight(20);
        if (yCur <= 10) {
            makePage();
        }
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
        yPos = yCur -= height;
    }

    private void makePage() throws IOException {
        contentStream.close();
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(font, fontSize);
        yPos = 830;
        yCur = 830;
        pageCount++;
    }

    private boolean hasEndWith(String path) {
        if (!path.startsWith("/")) {
            return false;
        }
        Path path1 = Path.of(path);
        if (!path1.isAbsolute()) {
            return false;
        }
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
