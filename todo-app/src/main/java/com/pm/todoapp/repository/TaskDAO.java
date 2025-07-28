package com.pm.todoapp.repository;

import com.pm.todoapp.model.Priority;
import com.pm.todoapp.model.Status;
import com.pm.todoapp.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskDAO {
    private final EntityManager entityManager;

    @Autowired
    public TaskDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Iterable<Task> findByBasicFilters(
            Priority priority,
            Status status,
            LocalDate startDate,
            LocalDate endDate
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);

        // SELECT * FROM task
        Root<Task> root = criteriaQuery.from(Task.class);

        // WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        if (priority != null) {
            predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
        }
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        if (startDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("taskDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("taskDate"), endDate));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Task> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }
}
