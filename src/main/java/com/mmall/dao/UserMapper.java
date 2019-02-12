package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    int checkEmail(String email);

    User selectLogin(@Param("userName") String userName,@Param("password") String password);

    String selectQuestionByUserName(String userName);

    int checkAnser(String userName, String question, String answer);

    int updatePasswordByUserName(String userName, String md5EncodeUtf8);

    int checkPassword(String passwordOld, Integer id);

    int checkEmailByUserId(String email, Integer id);
}