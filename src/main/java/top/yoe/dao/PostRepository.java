package top.yoe.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import top.yoe.pojo.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer>, JpaSpecificationExecutor<Post> {

    @Query(nativeQuery = true,
            value = "select * from posts where topic like ?1 or content like ?1 " +
                    "order by update_at desc limit ?2,?3")
    List<Post> findByKeyword(String keyword, Integer limit, Integer pageSize);

    List<Post> findByUserId(Integer userId);


}
