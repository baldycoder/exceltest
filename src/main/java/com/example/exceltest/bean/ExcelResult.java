package com.example.exceltest.bean;

import lombok.Data;
import lombok.ToString;

/**
 * @author ：a123145
 * @date ：Created in 2022/7/19 17:06
 * @description：ExcelResult
 * @modified By：`
 * @version: 1.0
 */
@Data
@ToString
public class ExcelResult {
    private Integer rowIndex;
    private String retCode;
    private String  errMsg;
}
