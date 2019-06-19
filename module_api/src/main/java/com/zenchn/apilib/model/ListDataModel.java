package com.zenchn.apilib.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 描    述：列表数据基础模型
 * 修订记录：
 *
 * @author HZJ
 */

public class ListDataModel<T> {

    public int total;
    @JSONField(name = "pageNum")
    public int pageNumber;

    public int pageSize;

    //总页数
    @JSONField(name = "pages")
    public int totalPages;

    //前一页
    public int prePage;
    //下一页
    public int nextPage;

    //是否为第一页
    public boolean isFirstPage;

    //是否为最后一页
    public boolean isLastPage;

    //是否有前一页
    public boolean hasPreviousPage;

    //是否有下一页
    public boolean hasNextPage;

    public List<T> list;

    @Override
    public String toString() {
        return "ListDataModel{" +
                "total=" + total +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", prePage=" + prePage +
                ", nextPage=" + nextPage +
                ", isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                ", hasPreviousPage=" + hasPreviousPage +
                ", hasNextPage=" + hasNextPage +
                ", list=" + list +
                '}';
    }
}
