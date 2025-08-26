package com.pm.todoapp.tasks.mapper;

import com.pm.todoapp.domain.teams.model.Team;
import com.pm.todoapp.tasks.dto.TaskTeamDTO;

public class TaskTeamMapper {
    public static TaskTeamDTO toDTO(Team team) {
        return TaskTeamDTO.builder()
                .id(team.getId())
                .build();
    }
}
