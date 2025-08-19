package com.pm.todoapp.tasks.dto;

import com.pm.todoapp.teams.dto.TeamResponseDTO;
import com.pm.todoapp.users.profile.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private String id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private TeamResponseDTO team;
    private Set<UserResponseDTO> assignees;
    private String taskDate;
    private String startTime;
    private String endTime;
}
