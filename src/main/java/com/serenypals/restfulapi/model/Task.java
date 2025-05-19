package com.serenypals.restfulapi.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task")
public class Task {

    @Id
    @Column(name = "id", length = 255, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "nama_task", length = 50, nullable = false)
    private String namaTask;

    @Column(name = "hadiah", nullable = false)
    private int hadiah;

    @Column(name = "hadiah_is_coin", nullable = false)
    private Boolean hadiahIsCoin;
    
    @OneToMany(mappedBy = "idTask", cascade = CascadeType.ALL)
    private Set<UserTask> givenTasks;
}
