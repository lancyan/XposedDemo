package com.wrbug.xposeddemo;

import java.util.List;

/**
 * Created by cyan on 2019/11/9.
 */

public interface PermissionListener {
    void granted();
    void denied(List<String> deniedList);
}
