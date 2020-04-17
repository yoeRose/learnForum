package top.yoe.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.yoe.pojo.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Category findByName(String name);
}
