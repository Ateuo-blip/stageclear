package io.stageclear.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private long total;     //总记录数
    private int pageNum;    //当前页
    private int pageSize;   //每页大小
    private List<T> records;//数据列表

    public static <T> PageResult<T> of(long total,List<T> records) {
        return PageResult.<T>builder()
                .total(total)
                .records(records)
                .build();
    }

    public static <T> PageResult<T> of(long total, int pageNum, int pageSize, List<T> records) {
        return PageResult.<T>builder()
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .records(records)
                .build();
    }
}
