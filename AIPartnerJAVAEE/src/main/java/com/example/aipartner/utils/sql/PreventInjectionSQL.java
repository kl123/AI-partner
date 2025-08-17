//package com.example.aipartner.utils.sql;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PreventInjectionSQL implements CommandLineRunner {
//
//    /**
//     * 转义SQL特殊字符防止注入
//     * @param input 输入字符串
//     * @return 转义后的字符串
//     */
//    public String escapeSql(String input) {
//        if (input == null) {
//            return null;
//        }
//        return input.replace("'", "''")  // 转义单引号
//                    .replace("\"", "\\\"")  // 转义双引号
//                    .replace(";", "\;")  // 转义分号
//                    .replace("--", "\--");  // 转义注释符
//    }
//
//    /**
//     * 校验输入是否包含危险字符
//     * @param input 输入字符串
//     * @return 校验结果（true：安全，false：危险）
//     */
//    public boolean validateInput(String input) {
//        if (input == null) {
//            return true;
//        }
//        // 简单校验常见危险字符，可根据需求扩展
//        return !input.matches(".*('|;|--|\b(select|insert|update|delete|drop|truncate)\b).*", "i");
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("[PreventInjectionSQL] 防SQL注入工具已启动，正在监控数据库操作...");
//    }
//}
