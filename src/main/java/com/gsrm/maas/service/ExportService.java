package com.gsrm.maas.service;

import com.gsrm.maas.entity.DemandeMaas;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Export des demandes au format Excel et PDF (Espace Gestionnaire GSRM). */
@Service
public class ExportService {

    private static final String[] HEADERS = {
            "N° demande", "Agence", "Escale", "Type", "Date service", "Heure",
            "Passagers", "Coût service", "Frais add.", "Coût total", "Greeter", "Statut"
    };
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] exportExcel(List<DemandeMaas> demandes) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Demandes MAAS");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (DemandeMaas d : demandes) {
                Row row = sheet.createRow(rowIdx++);
                int c = 0;
                row.createCell(c++).setCellValue(d.getNumeroDemande());
                row.createCell(c++).setCellValue(d.getAgence().getNomAgence());
                row.createCell(c++).setCellValue(d.getEscale().getCodeIata());
                row.createCell(c++).setCellValue(d.getTypeService().name());
                row.createCell(c++).setCellValue(d.getDateService().format(DATE_FMT));
                row.createCell(c++).setCellValue(d.getHeureService().toString());
                row.createCell(c++).setCellValue(d.getNombrePassagers());
                row.createCell(c++).setCellValue(d.getCoutService().doubleValue());
                row.createCell(c++).setCellValue(d.getFraisAdditionnels().doubleValue());
                row.createCell(c++).setCellValue(d.getCoutTotal().doubleValue());
                row.createCell(c++).setCellValue(d.getNomGreeter() == null ? "" : d.getNomGreeter());
                row.createCell(c).setCellValue(d.getStatut().getLibelle());
            }
            for (int i = 0; i < HEADERS.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportPdf(List<DemandeMaas> demandes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 20);
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("Demandes de service MAAS — GSRM", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        PdfPTable table = new PdfPTable(HEADERS.length);
        table.setWidthPercentage(100);
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 8);

        for (String h : HEADERS) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(20, 40, 90));
            cell.setPadding(4);
            table.addCell(cell);
        }
        for (DemandeMaas d : demandes) {
            table.addCell(new Phrase(d.getNumeroDemande(), cellFont));
            table.addCell(new Phrase(d.getAgence().getNomAgence(), cellFont));
            table.addCell(new Phrase(d.getEscale().getCodeIata(), cellFont));
            table.addCell(new Phrase(d.getTypeService().name(), cellFont));
            table.addCell(new Phrase(d.getDateService().format(DATE_FMT), cellFont));
            table.addCell(new Phrase(d.getHeureService().toString(), cellFont));
            table.addCell(new Phrase(String.valueOf(d.getNombrePassagers()), cellFont));
            table.addCell(new Phrase(d.getCoutService().toPlainString(), cellFont));
            table.addCell(new Phrase(d.getFraisAdditionnels().toPlainString(), cellFont));
            table.addCell(new Phrase(d.getCoutTotal().toPlainString(), cellFont));
            table.addCell(new Phrase(d.getNomGreeter() == null ? "" : d.getNomGreeter(), cellFont));
            table.addCell(new Phrase(d.getStatut().getLibelle(), cellFont));
        }
        document.add(table);
        document.close();
        return out.toByteArray();
    }
}
