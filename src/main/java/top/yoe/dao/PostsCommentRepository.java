package top.yoe.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import top.yoe.pojo.Post;
import top.yoe.pojo.PostsComment;

import java.util.List;

public interface PostsCommentRepository extends JpaRepository<PostsComment, Integer>, JpaSpecificationExecutor<PostsComment> {

    @Query(nativeQuery = true, value = "SELECT * FROM posts_comment WHERE post_id = ? ORDER BY create_at ASC LIMIT ?,?")
    List<PostsComment> findByPaging(Integer postId, Integer offset, Integer pageSize);

    @Query(nativeQuery = true, value = "SELECT count(*) FROM posts_comment WHERE post_id = ?")
    Integer countByPostId(Integer postId);
}
