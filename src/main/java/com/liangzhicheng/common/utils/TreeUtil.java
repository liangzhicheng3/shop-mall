package com.liangzhicheng.common.utils;

import com.liangzhicheng.common.exception.TransactionException;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {

    /**
     * 包装树形结构（全部属性），必须要有id、parentId、children
     * @param list
     * @return List
     */
    public static List toTree(List list) {
        try {
            if (list != null && list.size() > 0) {
                List resultList = new ArrayList();
                Map map = new HashMap();
                for (Object o : list) {
                    Class clazz = o.getClass();
                    Field id = clazz.getDeclaredField("id");
                    if (!id.isAccessible()) {
                        id.setAccessible(true);
                    }
                    Integer lId = (Integer) id.get(o);
                    map.put(lId, o);
                }
                for (Object o : map.keySet()) {
                    Integer cId = (Integer) o;
                    Object obj = map.get(cId);
                    Class clazz = obj.getClass();
                    Field pId = clazz.getDeclaredField("parentId");
                    if (!pId.isAccessible()) {
                        pId.setAccessible(true);
                    }
                    Integer id = (Integer) pId.get(obj);
                    if (null == map.get(id)) {
                        resultList.add(obj);
                    } else {
                        Object object = map.get(id);
                        Class clazz1 = object.getClass();
                        Field children = null;
                        try {
                            //没有children属性就到父类查找
                            children = clazz1.getDeclaredField("children");
                        } catch (Exception e) {
                            children = clazz1.getSuperclass().getDeclaredField("children");
                        }
                        if (!children.isAccessible()) {
                            children.setAccessible(true);
                        }
                        List childrenList = (List) children.get(object);
                        if (CollectionUtils.isEmpty(childrenList)) {
                            childrenList = new ArrayList();
                        }
                        childrenList.add(obj);
                        children.set(object, childrenList);
                    }
                }
                return resultList;
            }
        } catch (Exception e) {
            throw new TransactionException("未知异常，请联系管理员");
        }
        return null;
    }

}
