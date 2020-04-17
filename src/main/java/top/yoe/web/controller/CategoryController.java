package top.yoe.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yoe.dao.CategoryRepository;
import top.yoe.pojo.Category;
import top.yoe.util.AjaxUtil;
import top.yoe.util.StatusUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 添加分类
     * @param category
     * @return
     */
    @PostMapping("/add")
    public Map<String,Object> addCategory(Category category){
        if(category == null){
            return AjaxUtil.ajax_error(StatusUtil.FAIL);
        }
        Category existCategory = categoryRepository.findByName(category.getName());
        if(existCategory != null){
            return AjaxUtil.ajax_error(StatusUtil.CATEGORY_EXIST);
        }
        categoryRepository.save(category);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    @GetMapping("/delete")
    public Map<String,Object> deleteCategoryById(@RequestParam(name = "id")Integer categoryId){
        if(categoryId == null){
            return AjaxUtil.ajax_error(StatusUtil.FAIL);
        }
        Category category = categoryRepository.findOne(categoryId);
        if(category == null){
            return AjaxUtil.ajax_error(StatusUtil.CATEGORY_NOT_EXIST);
        }
        categoryRepository.delete(categoryId);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 显示所有分类
     * @return
     */
    @GetMapping("/findAllCategory")
    public Map<String, Object> findAllCategory(){
        List<Category> categories = categoryRepository.findAll();
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,categories);
    }
}
