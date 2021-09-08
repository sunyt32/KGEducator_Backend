package com.tsinghua.kgeducator.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.tsinghua.kgeducator.entity.User;
import com.tsinghua.kgeducator.mapper.UserMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService
{
    UserMapper userMapper;
    WebService webService;
    private static final int entityNum = 10;
    public UserService(UserMapper userMapper, WebService webService)
    {
        this.userMapper = userMapper;
        this.webService = webService;
    }
    public List<User> getAllUsers()
    {
        return userMapper.getAllUsers();
    }

    public User getUserById(Integer id)
    {
        return userMapper.getUserById(id);
    }

    public User getUserByEmail(String email)
    {
        return userMapper.getUserByEmail(email);
    }

    public Integer addUser(User user)
    {
        return userMapper.addUser(user);
    }

    public Integer updateUserById(User user)
    {
        return userMapper.updateUserById(user);
    }

    public Integer deleteUserById(Integer id)
    {
        return userMapper.deleteUserById(id);
    }

    public void deleteAllUsers()
    {
        userMapper.deleteAllUsers();
    }

    private List<String> getRelatedEntity(String entity, String subject)
    {
        List<String> relatedEntity = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("id", webService.getId());
        params.put("course", subject);
        params.put("name", entity);
        String url = "http://open.edukg.cn/opedukg/api/typeOpen/open/infoByInstanceName?id={id}&name={name}&course={course}";
        ResponseEntity<String> getForEntity = webService.restTemplate.getForEntity(url, String.class, params);
        HashMap<String, String> body = JSON.parseObject(getForEntity.getBody(),new TypeReference<HashMap<String, String>>(){});
        HashMap<String, String> data = JSON.parseObject(body.get("data"),new TypeReference<HashMap<String, String>>(){});
        List<HashMap<String, String>> content = JSON.parseObject(data.get("content"),new TypeReference<List<HashMap<String, String>>>(){});
        for(HashMap<String, String> item : content)
        {
            if(item.containsKey("subject_label"))
            {
                relatedEntity.add(String.format("[\"%s\", \"%s\"]", subject, item.get("subject_label")));
            }
            else if(item.containsKey("object_label"))
            {
                relatedEntity.add(String.format("[\"%s\", \"%s\"]", subject, item.get("object_label")));
            }
        }
        return relatedEntity;
    }

    public List<String> recommendEntity(User user)
    {
        HashMap<String, Integer> entities = new HashMap<>();
        List<String> relatedEntity = new ArrayList<>();
        List<Pair<String, Integer>> entityListForSort = new ArrayList<>();
        List<List<String>> userCollection = JSON.parseObject(user.collection, new TypeReference<List<List<String>>>(){});// 从用户收藏过但没有测试过的知识点中挑选出不到50%
        List<List<String>> userHistory = JSON.parseObject(user.history,new TypeReference<List<List<String>>>(){}); // 从用户浏览过但没有收藏过的知识点中挑选出剩余试题
        updateEntityCount(entities, userCollection);
        updateEntityCount(entities, userHistory);
        for(String entity : entities.keySet())
        {
            entityListForSort.add(new ImmutablePair<>(entity, entities.get(entity)));
        }
        entityListForSort.sort(Comparator.comparing(Pair::getRight, Comparator.reverseOrder()));
        if(entityListForSort.size() > entityNum)
            entityListForSort = entityListForSort.subList(0, entityNum);

        for(Pair<String, Integer> entityPair : entityListForSort)
        {
            relatedEntity.add(entityPair.getLeft());
        }
        return relatedEntity;
    }

    private void updateEntityCount(HashMap<String, Integer> entities, List<List<String>> userEntityList) {
        List<String> relatedEntity;
        for(List<String> userEntity : userEntityList)
        {
            relatedEntity = getRelatedEntity(userEntity.get(1), userEntity.get(0));
            for(String entity : relatedEntity)
            {
                if(entities.containsKey(entity))
                {
                    entities.put(entity, entities.get(entity) + 1);
                }
                else
                {
                    entities.put(entity, 1);
                }
            }
        }
    }

}