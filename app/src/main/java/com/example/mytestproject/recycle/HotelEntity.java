package com.example.mytestproject.recycle;

import java.util.ArrayList;

/**
 * Created by yushuangping on 2018/8/23.
 */

public class HotelEntity {

    public ArrayList<TagsEntity> allTagsList;

    public class TagsEntity {
        public String tagsName;
        public ArrayList<TagInfo> tagInfoList;

        public class TagInfo {
            public String tagName;
        }
    }

}
