package zerobase.sijak.persist.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Integer id;

    private String name;

    private String description;

    private Integer price;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    private String time;

    private String link;

    private String location;

    private String target;

    private String status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "classes")
    private List<Heart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "classes")
    private List<Review> reviews = new ArrayList<>();

}
