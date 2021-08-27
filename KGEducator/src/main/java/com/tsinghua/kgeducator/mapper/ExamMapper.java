package com.tsinghua.kgeducator.mapper;

import com.tsinghua.kgeducator.entity.Exam;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExamMapper
{
    List<Exam> getAllExams();

    List<Exam> getExamsByUserId(Integer userId);

    List<Exam> getWrongExamsByUserId(Integer userId);

    List<Exam> getExamsByPid(Integer pid);

    List<Exam> getExamsByName(String name);

    Integer addExam(Exam Exam);

}