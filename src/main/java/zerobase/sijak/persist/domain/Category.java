package zerobase.sijak.persist.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_name", nullable = false)
    private String name;


    @OneToMany(mappedBy = "category")
    private List<Class> classes = new ArrayList<>();


}
