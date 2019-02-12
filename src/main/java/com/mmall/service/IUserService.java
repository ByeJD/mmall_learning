package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Author: liuquanquan
 * Date  : 2019/2/12 15:26
 */
public interface IUserService {

    ServerResponse login(String name, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String userName);

    ServerResponse<String> checkAnswer(String userName, String question, String answer);

    ServerResponse<String> forgetResetPassword(String userName, String password, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);
}
