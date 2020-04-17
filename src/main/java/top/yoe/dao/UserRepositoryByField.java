package top.yoe.dao;

import org.springframework.data.repository.Repository;
import top.yoe.pojo.User;

public interface UserRepositoryByField extends Repository<User,Integer>{
    User findByUsername(String username);
}