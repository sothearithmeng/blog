package com.kshrd.blog.entity;

import com.kshrd.blog.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Example entity backing the {@code /api/v1/tasks} vertical slice used as a reference flow. */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "reporter_email")
    private String reporterEmail;

    public Task(String title, String description, String reporterEmail) {
        this.title = title;
        this.description = description;
        this.reporterEmail = reporterEmail;
        this.completed = false;
    }
}
