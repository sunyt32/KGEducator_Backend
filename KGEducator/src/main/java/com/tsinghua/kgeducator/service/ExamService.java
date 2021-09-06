package com.tsinghua.kgeducator.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tsinghua.kgeducator.entity.Exam;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.mapper.ExamMapper;
import com.tsinghua.kgeducator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.Collator;
import java.util.*;

@Service
public class ExamService
{
    private final ExamMapper examMapper;
    private final UserMapper userMapper;
    @Resource
    private RestTemplate restTemplate;
    private static final int examNum = 20;
    private String id;
    public ExamService(ExamMapper examMapper, UserMapper userMapper)
    {
        this.examMapper = examMapper;
        this.userMapper = userMapper;
    }
    @PostConstruct
    public void initRestTemplate()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("password", "Syt20001121");
        postParameters.add("phone", "18611157300");
        HttpEntity<String> request = new HttpEntity(postParameters, headers);
        String url = "http://open.edukg.cn/opedukg/api/typeAuth/user/login";
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(url, request, String.class);
        HashMap<String, String> body = JSON.parseObject(postForEntity.getBody(),new TypeReference<HashMap<String, String>>(){});
        id = body.get("id");
    }
    public List<Exam> getAllExams()
    {
        return examMapper.getAllExams();
    }
    List<Exam> getExamsByUserId(Integer userId)
    {
        return examMapper.getExamsByUserId(userId);
    }


    List<Exam> getExamsByPid(Integer pid)
    {
        return examMapper.getExamsByPid(pid);
    }

    List<Exam> getExamsByName(String name)
    {
        return examMapper.getExamsByName(name);
    }

    List<Exam> getWrongExamsByUserId(Integer userId)
    {
        return examMapper.getWrongExamsByUserId(userId);
    }

    public Integer addExam(Exam Exam)
    {
        return examMapper.addExam(Exam);
    }

    private String getNewExams(String name)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("uriName", name);
        String url = "http://open.edukg.cn/opedukg/api/typeOpen/open/questionListByUriName?id={id}&uriName={uriName}";
        ResponseEntity<String> getForEntity = restTemplate.getForEntity(url, String.class, params);
        HashMap<String, String> body = JSON.parseObject(getForEntity.getBody(),new TypeReference<HashMap<String, String>>(){});
        return body.get("data");
    }

    public List<String> recommendExams(Integer userId)
    {
        User user = userMapper.getUserById(userId);
        List<Exam> userExams = getWrongExamsByUserId(userId); // 从用户做过的错误习题中挑选出不到30%
        HashSet<List<String>> userCollection = JSON.parseObject(user.collection, new TypeReference<HashSet<List<String>>>(){});// 从用户收藏过但没有测试过的知识点中挑选出不到50%
        List<List<String>> userHistory = JSON.parseObject(user.history,new TypeReference<List<List<String>>>(){}); // 从用户浏览过但没有收藏过的知识点中挑选出剩余试题
        List<String> finalExams = new ArrayList<>();
        HashSet<String> entities = new HashSet<>();
        HashMap<String, String> inputExam;
        Collections.shuffle(userExams);
        for(Exam exam : userExams)
        {
            if(!entities.contains(exam.name))
            {
                entities.add(exam.name);
                inputExam = new HashMap<>();
                inputExam.put("name", exam.name);
                inputExam.put("qAnswer", exam.realAns);
                inputExam.put("id", exam.pid.toString());
                inputExam.put("qBody", exam.body);
                finalExams.add(inputExam.toString());
            }
            if(finalExams.size() > 0.3 * examNum)
            {
                break;
            }
        }
        for(List<String> collection : userCollection)
        {
            updateFinalExams(finalExams, entities, collection.get(1));
            if(finalExams.size() > 0.8 * examNum)
            {
                break;
            }
        }
        for(List<String> history : userHistory)
        {
            updateFinalExams(finalExams, entities, history.get(1));
            if(finalExams.size() > examNum)
            {
                break;
            }
        }
        while(finalExams.size() > examNum)
        {
            finalExams.remove(finalExams.size() - 1);
        }
        return finalExams;
    }

    private void updateFinalExams(List<String> finalExams, HashSet<String> entities, String entity)
    {
        if(!entities.contains(entity))
        {
            entities.add(entity);
            List<Map<String, String>> newExams = JSON.parseObject(getNewExams(entity), new TypeReference<List<Map<String, String>>>(){});
            if(newExams.size() == 1)
            {
                newExams.get(0).put("name", entity);
                finalExams.add(JSON.toJSONString(newExams.get(0)));
            }
            else if(newExams.size() > 1)
            {
                Collections.shuffle(newExams);
                newExams.get(0).put("name", entity);
                newExams.get(1).put("name", entity);
                finalExams.add(JSON.toJSONString(newExams.get(0)));
                finalExams.add(JSON.toJSONString(newExams.get(1)));
            }
        }
    }

}