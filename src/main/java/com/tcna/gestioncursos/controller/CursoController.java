package com.tcna.gestioncursos.controller;

import com.tcna.gestioncursos.entity.Curso;
import com.tcna.gestioncursos.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String listarCursos(Model model) {
        List<Curso> cursos = cursoRepository.findAll();
        model.addAttribute("cursos", cursos);
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
        }catch (Exception e){
            redirectAttributes.addAttribute("message", e.getMessage());
        }
        return"redirect:/cursos";
    }
}
