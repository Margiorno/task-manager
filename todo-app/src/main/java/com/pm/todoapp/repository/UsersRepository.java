package com.pm.todoapp.repository;

import com.pm.todoapp.model.Team;
import com.pm.todoapp.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByIdAndFriendsId(UUID id, UUID friendsId);

    default boolean areFriends(UUID userA, UUID userB) {
        return existsByIdAndFriendsId(userA, userB) || existsByIdAndFriendsId(userB, userA);
    }
}
