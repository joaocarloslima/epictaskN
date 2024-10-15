package br.com.fiap.epictaskn.task;

import br.com.fiap.epictaskn.user.User;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final RabbitTemplate rabbitTemplate;

    public TaskController(TaskService taskService, RabbitTemplate rabbitTemplate) {
        this.taskService = taskService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal OAuth2User pricipal){
        var user = (User) pricipal;
        var tasks = taskService.findPeding();
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/form")
    public String form(Task task){
        return "form";
    }

    @PostMapping("/task")
    public String create(@Valid Task task, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) return "form";

        taskService.create(task);

        rabbitTemplate.convertAndSend("email-queue", "Tarefa cadastrada: " + task.title);

        redirectAttributes.addFlashAttribute("message", "Tarefa cadastrada com sucesso");
        return "redirect:/";
    }

    @DeleteMapping("/task/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes){
        taskService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Tarefa apagada com sucesso");
        return "redirect:/";
    }

    @PutMapping("/task/catch/{id}")
    public String catchTask(@PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal){
        taskService.catchTask(id, (User) principal);
        return "redirect:/";
    }

    @PutMapping("/task/release/{id}")
    public String releaseTask(@PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal){
        taskService.releaseTask(id, (User) principal);
        return "redirect:/";
    }

    @PutMapping("/task/dec/{id}")
    public String decTask(@PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal){
        taskService.decTask(id, (User) principal);
        return "redirect:/";
    }

    @PutMapping("/task/inc/{id}")
    public String incTask(@PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal){
        taskService.incTask(id, (User) principal);
        return "redirect:/";
    }

}
