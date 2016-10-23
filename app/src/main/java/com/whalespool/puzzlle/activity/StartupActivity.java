package com.whalespool.puzzlle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.whalespool.puzzlle.PuzzlleApplication;
import com.whalespool.puzzlle.R;
import com.whalespool.puzzlle.module.User;
import com.whalespool.puzzlle.utils.ExUtil;
import com.whalespool.puzzlle.utils.SecurityUtil;
import com.whalespool.puzzlle.utils.SharedPreferencesUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class StartupActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText mEditName;
    @BindView(R.id.password)
    EditText mEditPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);
        User preUser = PuzzlleApplication.PreferencesUtil.getObject(SharedPreferencesUtil.SP_KEY_USER, User.class);
        if (preUser != null) {
            onLoginSuccess(preUser);
        }
    }

    public void onClick(View v) {
        final String name = mEditName.getText().toString();
        final String password = mEditPassword.getText().toString();
        if (ExUtil.confirmName(name)) {
            if (ExUtil.confirmPwd(password)) {
                // 登录验证逻辑函数
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("username", name);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (list.size() == 1) {
                            String pwd_MD5_Bmob = list.get(0).getPwd();
                            String pwd_MD5_Local = SecurityUtil.MD5_secure(password);
                            if (pwd_MD5_Bmob.equals(pwd_MD5_Local)) {
                                onLoginSuccess(list.get(0));
                            } else {
                                Toast.makeText(StartupActivity.this, "输入密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                            }
                        } else if (list.size() == 0) {
                            User temp = new User();
                            String pwd_MD5 = SecurityUtil.MD5_secure(password);
                            temp.setUsername(name);
                            temp.setPwd(pwd_MD5);
                            temp.setCreateTime(new BmobDate(new Date()));
                            registerToBmob(temp);
                        } else {
                            Toast.makeText(StartupActivity.this, "服务器存在多个同名用户", Toast.LENGTH_SHORT).show();
                        }
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Toast.makeText(StartupActivity.this, "密码格式错误，请重新输入", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(StartupActivity.this, "用户名格式错误，请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerToBmob(final User user) {
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(StartupActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    onLoginSuccess(user);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onLoginSuccess(User user) {
        //登陆成功后要做三件事：
        //1.更新Application中的User
        //2.启动MainActivity
        //3.清空输入框 [opt]
        PuzzlleApplication.PreferencesUtil.setObject(SharedPreferencesUtil.SP_KEY_USER, user);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
