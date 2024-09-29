package zerobase.sijak.persist.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

}
