package com.pm.todoapp.notifications.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FriendRequestNotificationDTO extends NotificationDTO {
    private FriendRequestUserDTO sender;
    private boolean resolved;
    private UUID requestId;
}
