package zerobase.sijak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.exception.IdNotExistException;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.repository.LectureRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    public List<Lecture> readLectures() {
        return lectureRepository.findAll();
    }

    public Slice<Lecture> readLectures(Pageable pageable) {
        return lectureRepository.findAll(pageable);
    }

    public Lecture readLecture(Integer id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));
    }


}
