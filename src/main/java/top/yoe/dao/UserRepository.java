package top.yoe.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import top.yoe.pojo.User;

import javax.persistence.criteria.CriteriaBuilder;


/**
 * 参数列表：
 *          1.参数1：声明映射的实体类
 *          2.参数2：声明实体列的主键的类型
 */
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String username);

    //分页
    Page<User> findByUsernameOrRealname(String username, String RealName, Pageable pageable);

    @Query(value = "insert into user_fallow(user_id,follow_id) values(?,?)",nativeQuery = true)
    void follow(Integer userID,Integer followID);

    @Modifying
    @Query(value = "update users set self_descn = ? where id = ? ",nativeQuery = true)
    Integer modifyUser(String descn, Integer userID);

    @Modifying
    @Query(value = "update users set is_banned = 1 where id = ?",nativeQuery = true)
    Integer banUserById(Integer userID);

    @Modifying
    @Query(value = "update users set is_banned = 0 where id = ?",nativeQuery = true)
    Integer cancelBanUserById(Integer userId);

    Long countUserByUsernameOrRealname(String username, String RealName);

    //获取所有管理员
    Page<User> findByAdmin(Integer is_admin,Pageable pageable);

    Long countUserByAdmin(Integer is_admin);

    Page<User> findByRealnameAndAdmin(String username,Integer is_admin,Pageable pageable);

    Long countUserByRealnameAndAdmin(String username,Integer is_admin);

    @Modifying
    @Query(value = "update users set realname = ? , self_descn = ? where id = ?",nativeQuery = true)
    Integer modifyUserRealNameAndSelfDescn(String realname,String descn,Integer userID);



}
