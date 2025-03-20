package com.lind.cursordemo.mapper;

import com.lind.cursordemo.entity.KnowledgeBase;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {
    
    @Select("SELECT * FROM knowledge_base")
    List<KnowledgeBase> findAll();
    
    @Select("SELECT * FROM knowledge_base WHERE id = #{id}")
    KnowledgeBase findById(Long id);
    
    @Insert("INSERT INTO knowledge_base (title, description, file_path, file_name, file_type, file_size, create_time, update_time) " +
            "VALUES (#{title}, #{description}, #{filePath}, #{fileName}, #{fileType}, #{fileSize}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KnowledgeBase knowledgeBase);
    
    @Update("UPDATE knowledge_base SET title = #{title}, description = #{description}, " +
            "file_path = #{filePath}, file_name = #{fileName}, file_type = #{fileType}, " +
            "file_size = #{fileSize}, update_time = #{updateTime} WHERE id = #{id}")
    int update(KnowledgeBase knowledgeBase);
    
    @Delete("DELETE FROM knowledge_base WHERE id = #{id}")
    int deleteById(Long id);
}