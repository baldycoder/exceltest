package com.example.exceltest.listener;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author ：a123145
 * @date ：Created in 2022/7/19 20:04
 * @description：自定义封装的实现类
 * @modified By：`
 * @version: 1.0
 */
@Slf4j
public class ReadListenerImpl<ExcelVO,ExcelMapper> extends MyReadListener{
    //父类构造函数有包装
    public ReadListenerImpl(String filePath, int batchCount, ExcelMapper mapper){
        super(filePath, batchCount,mapper);
    }

    //自定义实现
    @Override
    void saveData(List cachedDataList) {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        ((ExcelMapper)this.getMapper()).insertExcelTable(cachedDataList);
        log.info("存储数据库成功！");
    }
}
