//package com.liangzhicheng.config.aop;
//
//import com.liangzhicheng.common.exception.TransactionException;
//import com.liangzhicheng.config.mvc.filter.annotation.PermFilter;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//@Aspect
//@Component
//public class PermFilterAspect {
//
//    public static final int ADMIN = 1;
//
//    @Pointcut("@annotation(com.liangzhicheng.config.mvc.filter.annotation.PermFilter)")
//    public void permFilter() {
//
//    }
//
//    @Before("permFilter()")
//    public void dataFilter(JoinPoint point) {
//        //获取参数
//        Object params = point.getArgs()[0];
//        if (params != null && params instanceof Map) {
//            // TODO 获取用户信息
//            //如果不是超级管理员，则只能查询本部门及子部门数据
//            Long userId = 1L;
//            if (userId != ADMIN) {
//                Map map = (Map) params;
//                map.put("filterSql", getFilterSQL(userId, point));
//            }
//            return;
//        }
//        throw new TransactionException("数据权限接口的参数必须为Map类型，且不能为NULL");
//    }
//
//    private String getFilterSQL(Long userId, JoinPoint point) {
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        PermFilter dataFilter = signature.getMethod().getAnnotation(PermFilter.class);
//        String userAlias = dataFilter.userAlias();
//        String deptAlias = dataFilter.deptAlias();
//        StringBuilder filterSql = new StringBuilder();
//        if (StringUtils.isNotBlank(deptAlias)) {
//            //取出登录用户部门权限
//            String alias = getAliasByUser(userId);
//            if (StringUtils.isNotEmpty(alias)) {
//                filterSql.append(" and (");
//                filterSql.append(deptAlias);
//                filterSql.append(" in ");
//                filterSql.append(" ( ");
//                filterSql.append(alias);
//                filterSql.append(" ) ");
//                if (StringUtils.isNotBlank(userAlias)) {
//                    filterSql.append(" or ");
//                    filterSql.append(userAlias);
//                    filterSql.append("='");
//                    filterSql.append(userId);
//                    filterSql.append("' ");
//                }
//                filterSql.append(" ) ");
//            }
//        } else if (StringUtils.isNotBlank(userAlias)) {
//            filterSql.append(" and ");
//            filterSql.append(userAlias);
//            filterSql.append("='");
//            filterSql.append(userId);
//            filterSql.append("' ");
//        }
//        return filterSql.toString();
//    }
//
//    private String getAliasByUser(Long userId) {
//        List<Long> roleDeptIds = sysRoleDeptService.queryDeptIdsByUserId(userId);
//        StringBuilder roleDept = new StringBuilder();
//        String alias = "";
//        if (roleDeptIds != null && !roleDeptIds.isEmpty()) {
//            for (Long roleDeptId : roleDeptIds) {
//                roleDept.append(",");
//                roleDept.append("'");
//                roleDept.append(roleDeptId);
//                roleDept.append("'");
//            }
//            alias = roleDept.toString().substring(1, roleDept.length());
//        }
//        return alias;
//    }
//
//}
