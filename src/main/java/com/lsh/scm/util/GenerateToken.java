package com.lsh.scm.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateToken {
    public static String geneToken() {
        Date curr = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(curr);
    }

}
