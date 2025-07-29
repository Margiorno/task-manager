package com.pm.todoapp.controller;

import com.pm.todoapp.dto.TaskRequestDTO;
import com.pm.todoapp.dto.TaskResponseDTO;
import com.pm.todoapp.exceptions.TaskNotFoundException;
import com.pm.todoapp.mapper.TaskMapper;
import com.pm.todoapp.model.Priority;
import com.pm.todoapp.model.Status;
import com.pm.todoapp.model.Team;
import com.pm.todoapp.service.TaskService;
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

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/new")
    public String showNewTaskForm(Model model) {

        model.addAttribute("task", new TaskRequestDTO());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("formAction", "/task/new");
        model.addAttribute("isEditMode", false);

        return "task-form";
    }


    // TODO USERS IDENTIFICATION
    @PostMapping("/new")
    public String save(@ModelAttribute("task") @Valid TaskRequestDTO taskDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("formAction", "/task/new");
            return "task-form";
        }

        TaskResponseDTO response = taskService.save(taskDto);
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

        List<TaskResponseDTO> tasks;
        LocalDate centerDate = (selectedDate != null) ? selectedDate : LocalDate.now();

        tasks = switch (view) {
            case "calendar" -> taskService.findByDate(centerDate);
            case "filter" -> taskService.findByBasicFilters(priority, status, startDate, endDate);
            default -> taskService.findAll();
        };

        model.addAttribute("centerDate", centerDate);
        model.addAttribute("tasks", tasks);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("view", view);

        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);


        // TODO dynamic downloads of teams (at the moment PROWIZORKA :DDDDDDD)
        // TODO to rebuild this fragment
        Team teamA = new Team();
        teamA.setName("Team A");
        teamA.setId(UUID.fromString("1d8b9432-8362-4410-a128-2b8a2e5a1926"));

        Team teamB = new Team();
        teamB.setName("Team B");
        teamB.setId(UUID.fromString("1d8b9432-8362-4410-a128-2b8a2e5a1927"));

        Set<Team> allTeams = Set.of(teamA, teamB);

        model.addAttribute("allTeams", allTeams);

        if (teamId != null) {
            Optional<Team> selectedTeamOptional = allTeams.stream()
                    .filter(team -> team.getId().equals(teamId))
                    .findFirst();

            selectedTeamOptional.ifPresent(team -> {
                model.addAttribute("selectedTeamName", team.getName());
            });
        }
        model.addAttribute("selectedTeamId", teamId);
        ////////////////////////////////////////////////////////////////////////////

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
