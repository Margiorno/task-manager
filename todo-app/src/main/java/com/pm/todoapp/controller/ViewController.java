package com.pm.todoapp.controller;

import com.pm.todoapp.dto.TaskFetchScope;
import com.pm.todoapp.dto.TaskResponseDTO;
import com.pm.todoapp.dto.UserResponseDTO;
import com.pm.todoapp.model.Priority;
import com.pm.todoapp.model.Status;
import com.pm.todoapp.service.TaskService;
import com.pm.todoapp.service.TeamService;
import com.pm.todoapp.service.UsersService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class ViewController {

    private final TaskService taskService;
    private final TeamService teamService;
    private final UsersService usersService;

    @Autowired
    public ViewController(TaskService taskService, TeamService teamService, UsersService usersService) {
        this.taskService = taskService;
        this.teamService = teamService;
        this.usersService = usersService;
    }

    @Data
    public static class TaskFilterCriteria {
        private Priority priority;
        private Status status;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }

    @GetMapping
    public String showAllTasks(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "team", required = false) UUID teamId,
            @RequestParam(name = "scope", required = false, defaultValue = "USER_TASKS") TaskFetchScope scope,
            Model model) {

        List<TaskResponseDTO> tasks = (teamId != null)
                ? taskService.findByTeam(teamId, userId, scope)
                : taskService.findByUserId(userId);


        model.addAttribute("tasks", tasks);
        model.addAttribute("view", "all");
        populateCommonModelAttributes(model, userId, teamId, scope);

        return "dashboard";
    }

    @GetMapping("/calendar")
    public String showCalendarView(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
            @RequestParam(name = "team", required = false) UUID teamId,
            @RequestParam(name = "scope", required = false, defaultValue = "USER_TASKS") TaskFetchScope scope,
            Model model) {

        LocalDate centerDate = (selectedDate != null) ? selectedDate : LocalDate.now();

        List<TaskResponseDTO> tasks = taskService.findByDate(centerDate, userId, teamId, scope);

        model.addAttribute("tasks", tasks);
        model.addAttribute("view", "calendar");
        model.addAttribute("centerDate", centerDate);
        populateCommonModelAttributes(model, userId, teamId, scope);

        return "dashboard";
    }

    @GetMapping("/filter")
    public String showFilteredTasks(
            @AuthenticationPrincipal UUID userId,
            @ModelAttribute TaskFilterCriteria criteria,
            @RequestParam(name = "team", required = false) UUID teamId,
            @RequestParam(name = "scope", required = false, defaultValue = "USER_TASKS") TaskFetchScope scope,
            Model model) {

        List<TaskResponseDTO> tasks = taskService.findByBasicFilters(
                criteria.getPriority(), criteria.getStatus(), criteria.getStartDate(), criteria.getEndDate(),
                userId, teamId, scope);


        model.addAttribute("tasks", tasks);
        model.addAttribute("view", "filter");

        model.addAttribute("selectedPriority", criteria.getPriority());
        model.addAttribute("selectedStatus", criteria.getStatus());
        model.addAttribute("selectedStartDate", criteria.getStartDate());
        model.addAttribute("selectedEndDate", criteria.getEndDate());

        populateCommonModelAttributes(model, userId, teamId, scope);

        return "dashboard";
    }

    private void populateCommonModelAttributes(Model model, UUID userId, UUID teamId, TaskFetchScope scope) {
        if (teamId != null) {
            model.addAttribute("selectedTeamId", teamId.toString());
            model.addAttribute("selectedTeamName", teamService.findById(teamId).getName());
        }

        List<UserResponseDTO> teamMembers = (teamId != null)
                ? teamService.findUsersByTeamId(teamId)
                : Collections.emptyList();

        model.addAttribute("allTeams", teamService.findAllByUserId(userId));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("scopes", TaskFetchScope.values());
        model.addAttribute("selectedScope", scope);
        model.addAttribute("teamMembers", teamMembers);
    }

    @GetMapping("/chat")
    public String showChatView(Model model) {

        return "chat";
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UUID userId, Model model) {

        UserResponseDTO user = usersService.findById(userId);

        model.addAttribute("user", user);
        return "profile";
    }


}
