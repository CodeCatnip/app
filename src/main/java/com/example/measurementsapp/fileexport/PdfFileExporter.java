package com.example.measurementsapp.fileexport;

import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

@Component
public class PdfFileExporter implements FileExporter {

  public static final String BEAN_NAME = "pdfFileExporter";

  @Override
  public MeasurementFileData export(List<Measurement> measurements) throws IOException {
    var type = MeasurementType.Acceleration;
    if (!measurements.isEmpty()) {
      type = measurements.get(0).getType();
    }

    var document = new PDDocument();
    var page = new PDPage();
    document.addPage(page);

    int pageWidth = (int) page.getTrimBox().getWidth();
    int pageHeight = (int) page.getTrimBox().getHeight();

    var contentStream = new PDPageContentStream(document, page);
    contentStream.setStrokingColor(Color.DARK_GRAY);
    contentStream.setLineWidth(1);

    int initX = 50;
    int initY = pageHeight - 50;
    int cellHeight = 20;
    int cellWidth = (pageWidth - 2 * initX) / 3;

    var isHeader = true;
    for (var measurement : measurements) {
      if (isHeader) {
        contentStream.addRect(initX, initY, cellWidth, -cellHeight);
        contentStream.beginText();
        contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
        contentStream.showText("Czas [s]");
        contentStream.endText();
        initX += cellWidth;

        contentStream.addRect(initX, initY, cellWidth, -cellHeight);
        contentStream.beginText();
        contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
        contentStream.showText("Wartosc Pomiaru " + type.getUnit());
        contentStream.endText();
        initX += cellWidth;

        contentStream.addRect(initX, initY, cellWidth, -cellHeight);
        contentStream.beginText();
        contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
        contentStream.showText("Wartosc Pomiaru " + type.getUnit());
        contentStream.endText();

        initX = 50;
        initY -= cellHeight;
        isHeader = false;
        continue;
      }
      contentStream.addRect(initX, initY, cellWidth, -cellHeight);
      contentStream.beginText();
      contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
      contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
      contentStream.showText(measurement.getTimeInSeconds());
      contentStream.endText();
      initX += cellWidth;

      contentStream.addRect(initX, initY, cellWidth, -cellHeight);
      contentStream.beginText();
      contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
      contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
      contentStream.showText(measurement.getMeasureValueOne());
      contentStream.endText();
      initX += cellWidth;

      contentStream.addRect(initX, initY, cellWidth, -cellHeight);
      contentStream.beginText();
      contentStream.newLineAtOffset(initX + 10, initY - cellHeight + 10);
      contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
      contentStream.showText(measurement.getMeasureValueTwo());
      contentStream.endText();

      initX = 50;
      initY -= cellHeight;
    }

    contentStream.stroke();
    contentStream.close();

    var byteArrayOutputStream = new ByteArrayOutputStream();
    document.save(byteArrayOutputStream);
    document.close();
    return new MeasurementFileData("Pomiary.pdf", byteArrayOutputStream.toByteArray());
  }
}
