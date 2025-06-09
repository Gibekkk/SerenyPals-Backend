package com.serenypals.restfulapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.serenypals.restfulapi.enums.TaskStatus;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_task")
public class UserTask {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_user", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_task_user"))
    private User idUser;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_task", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_task_task"))
    private Task idTask;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus", nullable = false)
    private TaskStatus taskStatus;

    @Column(name = "completed_at", nullable = true)
    private LocalDate completedAt;

    @Column(name = "claimed_at", nullable = true)
    private LocalDate claimedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
