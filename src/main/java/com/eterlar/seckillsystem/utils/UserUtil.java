package com.eterlar.seckillsystem.utils;

import com.eterlar.seckillsystem.pojo.User;
import com.eterlar.seckillsystem.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户生成工具类
 * @author eterlar
 */
@Component
public class UserUtil {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 创建用户并存入 redis
     * @param count
     * @throws Exception
     */
    public void createUsers(int count) throws Exception {
        List<User> users = initUsers(count);
        System.out.println("create user");
        // 插入数据库
        Connection conn = getConn();
        String sql = "INSERT INTO t_user(id, nickname, password, salt, register_date, login_count) values(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        System.out.println(sql);
        for (User user : users) {
            pstmt.setLong(1, user.getId());
            pstmt.setString(2, user.getNickname());
            pstmt.setString(3,user.getPassword());
            pstmt.setString(4, user.getSalt());
            pstmt.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setInt(6,user.getLoginCount());
            pstmt.execute();
        }
        pstmt.close();
        conn.close();
        System.out.println("inserted into db...");

        // 登录，生成 userTicket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("D:\\All-Codes\\DevSpace\\SeckillSystem\\userInfo.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (User user : users) {
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                baos.write(buff, 0, len);
            }
            inputStream.close();
            baos.close();
            String response = baos.toString();
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObject();
            System.out.println("create userTicket: " + user.getId());

            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file: " + user.getId());
        }
        raf.close();

        System.out.println("finished...");
    }

    public Connection getConn() throws Exception {
        String url = "jdbc:mysql://192.168.10.6:3306/seckill?useUnicode=true&characterEncodings=UTF-8&serverTimezone=Asia/Shanghai";
        String userName = "root";
        String password = "root";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, userName, password);
    }

    /**
     * 生成用户数据集合
     * @param count
     * @return
     */
    public List<User> initUsers(int count) {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user"+i);
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        return users;
    }
}
