package com.example.exceltest.mapper;

import com.example.exceltest.bean.ExcelVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author ：a123145
 * @date ：Created in 2022/7/19 12:22
 * @description：Excel的操作dao
 * @modified By：`
 * @version: 1.0
 */

@Mapper
public interface ExcelMapper {

    int insertExcelTable(List<ExcelVO> cachedDataList);
}
