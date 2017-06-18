package com.mmall.service.Impl;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ADMIN on 2017/6/18.
 */
@Service("IUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServerResponse<User> login(String username, String password){
        int resultCount = userMapper.checkUsername(username);

        if(resultCount == 0){
            return  ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return  ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return  ServerResponse.createBySuccess("登陆成功",user);
    }

    public ServerResponse<String> register(User user){


        ServerResponse<String> validResponse = this.checkValid(user.getUsername(),Const.CURRENT_USER);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);

        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccess("注册成功");
    }


    /**
     * 返回前段的用户名和Email是否存在
     * @param str
     * @param type
     * @return
     */
    public ServerResponse<String> checkValid(String str,String type){

        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);

                if(resultCount == 0){
                    return  ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }else{
                int resultCount = userMapper.checkEmail(str);

                if(resultCount > 0){
                    return  ServerResponse.createByErrorMessage("邮箱已经存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccess("注册成功");
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户名不存在");
        }

    }


}

