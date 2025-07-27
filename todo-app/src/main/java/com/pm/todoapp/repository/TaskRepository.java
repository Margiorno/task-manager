package com.pm.todoapp.repository;

import com.pm.todoapp.model.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TaskRepository extends CrudRepository<Task, UUID> {
}
