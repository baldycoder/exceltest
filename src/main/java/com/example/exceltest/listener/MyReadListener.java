package com.example.exceltest.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.exceltest.bean.ExcelResult;
import com.example.exceltest.bean.ExcelVO;
import com.example.exceltest.mapper.ExcelMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ：a123145
 * @date ：Created in 2022/7/19 16:29
 * @description：自定义通用的easyExcel监听器
 * @modified By：`
 * @version: 1.0
 * 需要手动维护事务，遇到问题stop处理；另外此版本批处理数据库异常有bug
 */
@Data
@Slf4j
public class MyReadListener implements ReadListener<ExcelVO> {
    /**
     * 写返传文件路径
     */
    private String filePath = "";
    /**
     * 返传文件的writer
     */
    private ExcelWriter excelWriter = null;
    /**
     * 返传文件的每条记录，每次写完都清空
     */
    private List errCachedDataList = null;
    /**
     * 返传的sheet文件
     */
    private WriteSheet writeSheet = null;
    /**
     * 单次缓存的数据量
     */
    public   int BATCH_COUNT ;
    /**
     *临时存储
     */
    private List<ExcelVO> cachedDataList = null;
    /**
     * dao层的mapper
     */
    private ExcelMapper excelMapper;
    /**
     * 构造函数
     */
    public MyReadListener(String filePath, int batchCount, ExcelMapper excelMapper){
        this.filePath = filePath;
        this.BATCH_COUNT = batchCount;
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        this.excelMapper = excelMapper;
        //若上送返传文件路径，默认按上传路径生成返传文件，否则不生成返传文件
        if(filePath != null && !filePath.isEmpty()){
            this.errCachedDataList = ListUtils.newArrayListWithExpectedSize(1);
        }
    }

    private void saveData(List<ExcelVO> cachedDataList) {
        int count = 0;
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        count = excelMapper.insertExcelTable(cachedDataList);
        if(count <= 0)
            log.info("数据库处理失败！" + count);
        else
            log.info("存储数据库成功！");
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {

        log.info("====>第{}条数据异常==>{} " ,context.readRowHolder().getRowIndex(),context.readRowHolder().getCellMap() );
        int row = -1;
        int column = -1;
        if (exception instanceof ExcelDataConvertException){
            ExcelDataConvertException convertException=(ExcelDataConvertException) exception;

            row=convertException.getRowIndex();
            column=convertException.getColumnIndex();
            log.error("======>解析出错：{}行 {}列",row,column);
        }

        if(filePath != null && !filePath.isEmpty()){
            if(excelWriter == null){
                this.writeSheet = EasyExcelFactory.writerSheet("处理结果").build();
                this.excelWriter = EasyExcelFactory.write(filePath).head(ExcelResult.class).build();
            }
            ExcelResult excelResult = new ExcelResult();
            excelResult.setRetCode("999");
            excelResult.setErrMsg(exception.getMessage());
            excelResult.setRowIndex(context.readRowHolder().getRowIndex());
            errCachedDataList.add(excelResult);

            excelWriter.write(errCachedDataList,writeSheet);
            errCachedDataList = ListUtils.newArrayListWithExpectedSize(1);
//            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }


    @Override
    public void invoke(ExcelVO data, AnalysisContext analysisContext) {
        this.dataValidate(data,analysisContext.readRowHolder().getRowIndex());
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            System.out.println("cachedDataList.size() = " + cachedDataList.size());
            saveData(cachedDataList);
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //剩余记录处理
        if(cachedDataList.size() > 0)
            saveData( cachedDataList);
        this.finish();
    }

    public void dataValidate(ExcelVO data,int rowNum){
        if(data.getAge() < 0){
            throw new RuntimeException("第" + rowNum +"行 Age不能为负数");
        }
    }


    public void finish(){
        if(excelWriter!=null){
            excelWriter.finish();
        }
    }

}
