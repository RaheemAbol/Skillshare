package com.abolent.skillSharev2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Buddy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String bio;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    private Set<Skill> skills = new HashSet<>();


    @OneToMany(mappedBy = "buddy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();


}
