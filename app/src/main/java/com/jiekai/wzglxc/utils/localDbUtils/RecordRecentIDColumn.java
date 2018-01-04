package com.jiekai.wzglxc.utils.localDbUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laowu on 2017/12/23.
 * 盘库的每一类设备数量的本地数据表
 */

public class RecordRecentIDColumn extends DataBaseColumn {
    public final static String TABLE_NAME = "RECORD_RECENT_ID";
    private Map<String, String> columnMap;

    public final static String BH = "BH";  //设备类别
    public final static String ID = "id";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, String> getColumnMap() {
        if(columnMap == null) {
            columnMap = new HashMap<String, String>();
        }
        columnMap.put(ID, "integer(4) PRIMARY KEY");
        columnMap.put(BH, "text");
        return columnMap;
    }
}
