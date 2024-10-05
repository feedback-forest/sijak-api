package zerobase.sijak.persist.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Educate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "educate_id")
    private Integer id;

    @Size(max = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    public Educate(Lecture lecture, String content) {
        this.content = content;
        this.lecture = lecture;
    }

}
