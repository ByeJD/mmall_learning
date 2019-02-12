package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Author: liuquanquan
 * Date  : 2019/2/12 15:27
 */
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String name, String password) {

        int resCount = userMapper.checkUserName(name);

        if (resCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(name, password);

        if (user == null) {
            ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> resp = checkValid(user.getUsername(), Const.USERNAME);

        if (!resp.isSuccess()) {
            return resp;
        }

        resp = checkValid(user.getEmail(), Const.EMAIL);
        if (!resp.isSuccess()) {
            return resp;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resCount = userMapper.insert(user);
        if (resCount > 0) {
            return ServerResponse.createBySuccessMessage("注册成功");
        } else {
            return ServerResponse.createByErrorMessage("注册失败");
        }
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {

        if (StringUtils.isBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resCount = userMapper.checkUserName(str);
                if (resCount != 0) {
                    return ServerResponse.createByErrorMessage("用户名存在");
                }
            }

            if (Const.EMAIL.equals(type)) {
                int resCount = userMapper.checkEmail(str);
                if (resCount != 0) {
                    return ServerResponse.createByErrorMessage("邮箱已被注册");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String userName) {

        ServerResponse<String> response = checkValid(userName, Const.USERNAME);

        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUserName(userName);

        if (StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String userName, String question, String answer) {
        int resCount = userMapper.checkAnser(userName,question,answer);

        if (resCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(forgetToken,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }else {
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String userName, String passwordNew, String forgetToken) {

        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("forgetToken不能为空");
        }

        ServerResponse<String> response = checkValid(userName, Const.USERNAME);

        if (response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(forgetToken);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token过期或者token不存在");
        }

        if (StringUtils.equals(forgetToken,token)){
            int resCount = userMapper.updatePasswordByUserName(userName,MD5Util.MD5EncodeUtf8(passwordNew));
            if (resCount > 0){
                return ServerResponse.createBySuccessMessage("密码重置成功");
            }else {
                return ServerResponse.createByErrorMessage("密码重置失败");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误");
        }
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        Integer id = user.getId();
        int resCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),id);

        if (resCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));

        resCount = userMapper.updateByPrimaryKeySelective(user);

        if (resCount > 0){
            return ServerResponse.createBySuccessMessage("密码重置成功");
        }else {
            return ServerResponse.createByErrorMessage("密码更新失败");
        }
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        user.setUsername(null);
        int resCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if (resCount > 0){
            return ServerResponse.createByErrorMessage("email已经存在");
        }

        User updateUser = new User();
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setId(user.getId());
        resCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resCount > 0){
            return ServerResponse.createBySuccess("更新成功",updateUser);
        }else {
            return ServerResponse.createByErrorMessage("更新失败");
        }
    }
}
