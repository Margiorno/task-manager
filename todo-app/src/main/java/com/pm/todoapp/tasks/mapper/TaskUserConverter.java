package com.pm.todoapp.tasks.mapper;

import com.pm.todoapp.core.user.dto.UserDTO;
import com.pm.todoapp.core.user.model.User;
import com.pm.todoapp.core.user.port.UserProviderPort;
import com.pm.todoapp.core.user.port.UserValidationPort;
import com.pm.todoapp.tasks.dto.TaskUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskUserConverter {

    private final UserValidationPort userValidationPort;
    private final UserProviderPort userProviderPort;

    @Autowired
    public TaskUserConverter(UserValidationPort userValidationPort, UserProviderPort userProviderPort) {
        this.userValidationPort = userValidationPort;
        this.userProviderPort = userProviderPort;
    }

    public TaskUserDTO toDTO(User user){

        userValidationPort.ensureUserExistsById(user.getId());
        UserDTO userDTO = userProviderPort.getUserById(user.getId());

        return TaskUserDTO.builder()
                .id(user.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .profilePicturePath(userDTO.getProfilePicturePath())
                .build();
    }
}
