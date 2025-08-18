package com.pm.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pm.todoapp.model.NotificationType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dtoType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FriendRequestNotificationDTO.class, name = "FRIEND_REQUEST")
})
public class NotificationDTO {
    private UUID id;
    private NotificationType type;
    private String message;
    private String notificationTime;
    private boolean isRead;
}
