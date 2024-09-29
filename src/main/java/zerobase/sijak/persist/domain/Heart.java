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
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

}
