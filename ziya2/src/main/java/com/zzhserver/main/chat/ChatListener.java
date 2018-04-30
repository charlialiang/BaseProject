package com.zzhserver.main.chat;

import com.zzhserver.global.BaseListener;

/**
 * Created by Administrator on 2017/12/30 0030.
 */

public interface ChatListener extends BaseListener{
    void update(int position);
    void finish();
}
