package com.example.exceltest.listener;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.exception.ExcelRuntimeException;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.exceltest.bean.ExcelResult;
import com.example.exceltest.bean.ExcelVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * @author ：a123145
 * @date ：Created in 2022/7/19 16:29
 * @description：自定义通用的easyExcel监听器
 * @modified By：`
 * @version: 1.0
 */
@Data
public abstract class MyReadListener<T,M> implements ReadListener<T> {
    /**
     * 文件路径
     */
    private String filePath = "";// "/Users/a123145/progrom/tmp/" + "result.xlsx";
    /**
     * 返传文件的writer
     */
    private ExcelWriter excelWriter = null;//EasyExcelFactory.write(filePath).head(ExcelResult.class).build();
    /**
     * 返传文件的每条记录，每次写完都清空
     */
    private List errCachedDataList = null;//ListUtils.newArrayListWithExpectedSize(1);
    /**
     * 返传的sheet文件
     */
    private WriteSheet writeSheet = null;//EasyExcelFactory.writerSheet("处理结果").build();
    /**
     * Mapper的泛型
     */
    private M mapper;
    /**
     * 单次缓存的数据量
     */
    public   int BATCH_COUNT ;
    /**
     *临时存储
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 构造函数
     */
    public MyReadListener(String filePath, int batchCount, M m){
        this.filePath = filePath;
        this.BATCH_COUNT = batchCount;
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        this.writeSheet = EasyExcelFactory.writerSheet("处理结果").build();
        this.errCachedDataList = ListUtils.newArrayListWithExpectedSize(1);
        this.excelWriter = EasyExcelFactory.write(filePath).head(ExcelResult.class).build();
        this.mapper = m;
    }

    abstract void saveData(List<T> cachedDataList);

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        errCachedDataList.add(context.readRowHolder().getRowIndex());
        errCachedDataList.add("999");
        errCachedDataList.add(exception.getMessage());
        excelWriter.write(errCachedDataList,writeSheet);
        errCachedDataList = ListUtils.newArrayListWithExpectedSize(1);
    }


    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        cachedDataList.add(t);
        System.out.println("data = " + t );
        if (cachedDataList.size() >= BATCH_COUNT) {
            System.out.println("cachedDataList.size() = " + cachedDataList.size());
            saveData(cachedDataList);
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if(cachedDataList.size() > 0)
            saveData( cachedDataList);
        this.finish();
    }


    public void finish(){
        if(excelWriter!=null){
            excelWriter.finish();
        }
    }

}
