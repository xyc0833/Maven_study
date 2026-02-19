package com.test;


import com.test.entity.Student;
import com.test.mapper.TestMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World");
        Student student = new Student(2003,"小明", "男");
        System.out.println(student);

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
        try(SqlSession session = factory.openSession(true)){
            TestMapper mapper = session.getMapper(TestMapper.class);
            System.out.println(mapper.getStudentBysid(1001));
        }
    }
}