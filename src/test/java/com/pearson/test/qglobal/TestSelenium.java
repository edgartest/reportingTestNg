package com.pearson.test.qglobal;

import com.pearson.common.enums.QGUrl;

public class TestSelenium {
    public TestSelenium() {
    }

    public static void main(String[] args) {
        String url = QGUrl.LOGIN.getUrl();
        if (QGUrl.LOGIN.isUnique("unknownlogin.seam")) {
            System.out.println(QGUrl.LOGIN.findNameByUrl("unknownlogin.seam"));
        }

    }
}
