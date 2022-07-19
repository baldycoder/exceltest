package com.example.exceltest.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.exception.ExcelRuntimeException;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.example.exceltest.bean.ExcelVO;
import com.example.exceltest.mapper.ExcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：a123145
 * @date ：Created in 2022/7/19 12:09
 * @description：excel的请求处理
 * @modified By：`
 * @version: 1.0
 */
@RestController
@Slf4j
public class ExcelHandle {

    @Autowired
    ExcelMapper excelMapper;

    @RequestMapping("/import")
    public String readExcel(){
        // 写法2：
        // 匿名内部类 不用额外写一个DemoDataListener
        String fileName = "/Users/a123145/progrom/tmp/demo1.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, ExcelVO.class, new ReadListener<ExcelVO>() {
            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 5;
            /**
             *临时存储
             */

            private List<ExcelVO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(ExcelVO data, AnalysisContext context) {
                cachedDataList.add(data);
                System.out.println("data = " + data );
                if (cachedDataList.size() >= BATCH_COUNT) {
                    System.out.println("cachedDataList.size() = " + cachedDataList.size());
                    saveData(cachedDataList);
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                if(cachedDataList.size()>0){
                    System.out.println("doAfter----cachedDataList.size() = " + cachedDataList.size());
                    saveData(cachedDataList);
                }
            }

            @Override
            public void onException(Exception exception, AnalysisContext context) throws Exception {
                log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
                // 如果是某一个单元格的转换异常 能获取到具体行号
                // 如果要获取头的信息 配合invokeHeadMap使用
                if (exception instanceof ExcelRuntimeException) {
                    ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
                    log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                            excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
                }
            }

            /**
             * 加上存储数据库
             */
            private void saveData(List<ExcelVO> cachedDataList) {
                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
                excelMapper.insertExcelTable(cachedDataList);
                log.info("存储数据库成功！");
            }
        }).sheet().doRead();

        return "success";
    }
}
