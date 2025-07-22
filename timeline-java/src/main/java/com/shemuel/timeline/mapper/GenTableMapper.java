package com.shemuel.timeline.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.shemuel.timeline.entity.GenTable;
import com.shemuel.timeline.entity.GenTableColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GenTableMapper {
    
    List<GenTable> selectGenTableList(GenTable genTable);

    int deleteGenTableByIds(Long[] tableIds);

    @Select("SELECT " +
            "column_name, " +
            "CASE WHEN column_key = 'PRI' THEN column_key ELSE '' END AS column_key, " +
            "data_type AS column_type, " +
            "column_comment, " +
            "CASE WHEN is_nullable = 'NO' THEN '1' ELSE '0' END AS is_required, " +
            "ordinal_position AS sort " +
            "FROM information_schema.columns " +
            "WHERE table_schema = (SELECT DATABASE()) " +
            "AND table_name = #{tableName} " +
            "ORDER BY ordinal_position")
    List<GenTableColumn> selectDbTableColumns(@Param("tableName") String tableName);
    
    void insertGenTable(GenTable genTable);
    
    GenTable selectGenTableById(Long tableId);
    
    List<GenTableColumn> selectGenTableColumns(Long tableId);
    
    void insertGenTableColumn(GenTableColumn column);

    @InterceptorIgnore(tenantLine = "true")
    List<GenTable> selectDbTableList(GenTable genTable);
    
    int insertGenTableBatch(@Param("columns") List<GenTableColumn> columns);
    
    int selectDbTableCount(GenTable genTable);
    
    GenTable selectGenTableByName(String tableName);
    
    void deleteGenTableColumns(List<Long> columnIds);
    
    void updateGenTableColumns(List<GenTableColumn> columns);
    
    void updateGenTable(GenTable table);

    void deleteGenTableColumnsByTableIds(Long[] tableIds);
}