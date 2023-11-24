package com.tcna.gestioncursos.reports;

import com.tcna.gestioncursos.entity.Curso;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class CursosExporterExcel {

    private XSSFWorkbook workBook;
    private XSSFSheet sheet;
    private List<Curso> cursos;

    public CursosExporterExcel(List<Curso> cursos) {
        this.cursos = cursos;
        workBook = new XSSFWorkbook();
    }

   private void writeHeaderLine(){
        sheet = workBook.createSheet("Cursos");
        Row row = sheet.createRow(0);
       CellStyle style = workBook.createCellStyle();
       XSSFFont font = workBook.createFont();
       font.setBold(true);
       font.setFontHeight(16);
       style.setFont(font);

       createCell(row, 0, "ID", style);
       createCell(row, 1, "Titulo", style);
       createCell(row, 2, "Descripcion", style);
       createCell(row, 3, "Nivel", style);
       createCell(row, 4, "Estado de publicacion", style);
   }

   private void createCell(Row row, int columnCount, Object value, CellStyle style){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if (value instanceof Integer){
            cell.setCellValue((Integer)value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean)value);
        }else{
            cell.setCellValue((String)value);
        }
        cell.setCellStyle(style);
   }

   private void writeDataLine(){
        int rowCount = 1;

        CellStyle style = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Curso curso : cursos){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, curso.getId(), style);
            createCell(row, columnCount++, curso.getTitulo(), style);
            createCell(row, columnCount++, curso.getDescripcion(), style);
            createCell(row, columnCount++,curso.getNivel(), style);
            createCell(row, columnCount++,curso.isPublicado(), style);
        }
   }

   public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLine();

       ServletOutputStream outputStream = response.getOutputStream();
       workBook.write(outputStream);
       workBook.close();
       outputStream.close();
   }
}