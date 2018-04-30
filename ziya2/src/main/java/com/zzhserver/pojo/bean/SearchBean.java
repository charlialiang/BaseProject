package com.zzhserver.pojo.bean;

/**
 * Created by Administrator on 2018/1/15 0015.
 */

public class SearchBean {
    public String typeMsg;//[SEARCH_ID]: [SEARCH_NAME]: [SEARCH_ONLINE]:
    public int page;//找第几页的
    public int pageSize;//最大页码

    public SearchBean() {
    }

    public SearchBean(String typeMsg, int page, int pageSize) {
        this.typeMsg = typeMsg;
        this.page = page;
        this.pageSize = pageSize;
    }
}
