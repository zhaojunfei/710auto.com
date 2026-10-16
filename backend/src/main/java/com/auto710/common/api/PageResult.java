package com.auto710.common.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {

    private long total;
    private long page;
    private long size;
    private long pages;
    private List<T> records = new ArrayList<>();

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.setTotal(page.getTotal());
        r.setPage(page.getCurrent());
        r.setSize(page.getSize());
        r.setPages(page.getPages());
        r.setRecords(page.getRecords());
        return r;
    }

    public static <T> PageResult<T> of(long total, long page, long size, List<T> records) {
        PageResult<T> r = new PageResult<>();
        r.setTotal(total);
        r.setPage(page);
        r.setSize(size);
        r.setPages(size == 0 ? 0 : (total + size - 1) / size);
        r.setRecords(records);
        return r;
    }
}
