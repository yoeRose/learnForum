package top.yoe.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.yoe.pojo.Category;
import top.yoe.pojo.Resources;
import top.yoe.pojo.User;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources,Integer> {
    Page<Resources> findByNameLike(String like, Pageable pageable);

    Long countResourcesByNameLike(String like);

    Page<Resources> findByCategory(Category category, Pageable pageable);

    Long countResourcesByCategory(Category category);

    List<Resources> findByUploader(User uploader);
}
