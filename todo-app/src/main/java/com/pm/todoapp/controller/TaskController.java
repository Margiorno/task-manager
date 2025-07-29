package com.pm.todoapp.controller;

import com.pm.todoapp.dto.TaskRequestDTO;
import com.pm.todoapp.dto.TaskResponseDTO;
import com.pm.todoapp.exceptions.TaskNotFoundException;
import com.pm.todoapp.mapper.TaskMapper;
import com.pm.todoapp.model.Priority;
import com.pm.todoapp.model.Status;
import com.pm.todoapp.model.Team;
import com.pm.todoapp.service.TaskService;
import com.pm.todoapp.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final TeamService teamService;

    @Autowired
    public TaskController(TaskService taskService, TeamService teamService) {
        this.taskService = taskService;
        this.teamService = teamService;
    }

    @GetMapping("/new")
    public String showNewTaskForm(@RequestParam(name = "team", required = false) UUID teamId,
                                  Model model) {

        model.addAttribute("task", new TaskRequestDTO());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("formAction", "/task/new");
        model.addAttribute("isEditMode", false);

        if (teamId != null) {
            model.addAttribute("teamId", teamId);
        }

        return "task-form";
    }


    // TODO USERS IDENTIFICATION
    @PostMapping("/new")
    public String save(@ModelAttribute("task") @Valid TaskRequestDTO taskDto,
                       @RequestParam(name = "team", required = false) UUID teamId,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("formAction", "/task/new");
            return "task-form";
        }

        System.out.println(teamId);

        //TODO finish team assigning inside service
        TaskResponseDTO response = taskService.save(taskDto, teamId);

        model.addAttribute("taskResponse", response);
        model.addAttribute("message", "Task saved successfully!");

        return "task-details";
    }

    @GetMapping("/list")
    public String showTasks(
            @RequestParam(name = "view", defaultValue = "all") String view,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
            @RequestParam(name = "priority", required = false) Priority priority,
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "team", required = false) UUID teamId,
            Model model) {


        // TODO to rebuild this fragment
        List<Team> allTeams = teamService.findAll();

        model.addAttribute("allTeams", allTeams);

        model.addAttribute("selectedTeamId", teamId);
        ////////////////////////////////////////////////////////////////////////////


        List<TaskResponseDTO> tasks;
        LocalDate centerDate = (selectedDate != null) ? selectedDate : LocalDate.now();

        if (teamId != null) {
            tasks = taskService.findByTeam(teamId);
        } else {
            tasks = switch (view) {
                case "calendar" -> taskService.findByDate(centerDate);
                case "filter" -> taskService.findByBasicFilters(priority, status, startDate, endDate);
                default -> taskService.findAll();
            };
        }

        model.addAttribute("centerDate", centerDate);
        model.addAttribute("tasks", tasks);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("view", view);

        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);

        return "task-list";
    }

    @GetMapping("/{id}")
    public String showTask(@PathVariable UUID id, Model model) {

        TaskResponseDTO response = taskService.findById(id);
        model.addAttribute("taskResponse", response);
        return "task-details";
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable UUID id, Model model) {

        TaskResponseDTO taskResponse = taskService.findById(id);
        TaskRequestDTO taskRequest = TaskMapper.fromResponseToRequest(taskResponse);

        model.addAttribute("task", taskRequest);
        model.addAttribute("taskId", id);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());

        model.addAttribute("isEditMode", true);

        return "task-form";
    }

    //TODO userId
    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable UUID id,
                             @ModelAttribute("task") @Valid TaskRequestDTO taskDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("taskId", id);
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("isEditMode", true);
            return "task-form";
        }

        TaskResponseDTO updated = taskService.update(taskDto, id);
        model.addAttribute("taskResponse", updated);
        model.addAttribute("message", "Task updated successfully!");

        return "task-details";
    }

}
