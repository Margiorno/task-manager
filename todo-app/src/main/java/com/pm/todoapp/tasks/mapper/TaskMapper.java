package com.pm.todoapp.tasks.mapper;

import com.pm.todoapp.teams.mapper.TeamMapper;
import com.pm.todoapp.users.profile.mapper.UserMapper;
import com.pm.todoapp.tasks.dto.TaskRequestDTO;
import com.pm.todoapp.tasks.dto.TaskResponseDTO;
import com.pm.todoapp.tasks.model.Task;
import com.pm.todoapp.users.profile.model.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskMapper {
    public static Task toEntity(TaskRequestDTO dto, Set<User> users) {

        return Task.builder()
                .title(dto.getTitle())
                .assignees(users)
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .taskDate(dto.getTaskDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    public static Task toEntity(TaskRequestDTO dto, Set<User> users, UUID taskId) {
        Task task = toEntity(dto, users);
        task.setId(taskId);

        return task;
    }

    public static TaskResponseDTO toResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .assignees(task.getAssignees().stream().map(UserMapper::toUserResponseDTO)
                                .collect(Collectors.toSet()))
                .team(task.getTeam() == null ? null : TeamMapper.toResponseDTO(task.getTeam()))
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .taskDate(task.getTaskDate() != null ? task.getTaskDate().toString() : null)
                .startTime(task.getStartTime() != null ? task.getStartTime().toString() : null)
                .endTime(task.getEndTime() != null ? task.getEndTime().toString() : null)
                .build();
    }

    public static TaskRequestDTO toRequestDto(Task task) {
        return TaskRequestDTO.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .taskDate(task.getTaskDate())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .build();
    }
}
