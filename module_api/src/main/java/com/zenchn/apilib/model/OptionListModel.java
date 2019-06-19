package com.zenchn.apilib.model;

import java.util.List;

/**
 * 描    述：后端维护的选项列表数据模型
 * 修订记录：
 *
 * @author HZJ
 */
public class OptionListModel<T> {

    public String key;
    public List<T> list;

    @Override
    public String toString() {
        return "OptionListModel{" +
                "key='" + key + '\'' +
                ", list=" + list +
                '}';
    }
}
