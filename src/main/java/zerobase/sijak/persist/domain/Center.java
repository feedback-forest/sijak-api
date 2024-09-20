package zerobase.sijak.persist.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "center_name", unique = true, nullable = false)
    private String centerName;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "center")
    private List<Class> classes = new ArrayList<>();

}
