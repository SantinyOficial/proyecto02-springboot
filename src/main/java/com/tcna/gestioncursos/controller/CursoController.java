package com.tcna.gestioncursos.controller;

import com.tcna.gestioncursos.entity.Curso;
import com.tcna.gestioncursos.reports.CursoExporterPDF;
import com.tcna.gestioncursos.reports.CursosExporterExcel;
import com.tcna.gestioncursos.repository.CursoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping("/")
    public String home() {

        return "redirect:/cursos";
    }

    @GetMapping("/cursos")
    public String listarCursos(Model model, @Param("keyword") String keyword, @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "3") int size) {
        try {
            List<Curso> cursos = new ArrayList<>();
            Pageable paging = PageRequest.of(page-1, size);

            Page<Curso> pageCursos = null;
            if (keyword == null){
                pageCursos = cursoRepository.findAll(paging);
            }else{
                pageCursos = cursoRepository.findByTituloContainingIgnoreCase(keyword, paging);
                model.addAttribute("Keyword", keyword);
            }

            cursos = pageCursos.getContent();

            model.addAttribute("cursos", cursos);
            model.addAttribute("currentPage", pageCursos.getNumber()+1);
            model.addAttribute("totalItems", pageCursos.getTotalElements());
            model.addAttribute("totalPages", pageCursos.getTotalPages());
            model.addAttribute("pageSize", size);

        }catch(Exception exception){
            model.addAttribute("message", exception.getMessage());
        }

        return "cursos";
    }

    @GetMapping("/cursos/nuevo")
    public String agregarCursos(Model model) {
        Curso curso = new Curso();
        curso.setPublicado(true);
        model.addAttribute("curso", curso);
        model.addAttribute("pageTittle", "Nuevo curso");
        return "curso_form";
    }

    @PostMapping("/cursos/save")
    public String guardarCurso(Curso curso, RedirectAttributes redirectAttributes) {
        try {
            cursoRepository.save(curso);
            redirectAttributes.addFlashAttribute("message", "Curso guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }
        return "redirect:/cursos";
    }

    @GetMapping("/export/pdf")
    public void generarReportePDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=cursos" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Curso> cursos = cursoRepository.findAll();
        CursoExporterPDF exporterPDF = new CursoExporterPDF(cursos);
        exporterPDF.export(response);
    }

    @GetMapping("/export/excel")
    public void generarReporteExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=cursos" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Curso> cursos = cursoRepository.findAll();
        CursosExporterExcel exporterExcel = new CursosExporterExcel(cursos);
        exporterExcel.export(response);
    }
}
