package com.example.aipartner.pojo.IndividualPlaning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCourse {
  private static final Logger log = LoggerFactory.getLogger(UserCourse.class);
  private Integer id;
  private Integer userId;
  @JsonIgnore
  private String courseStr;
  private Map<String, Object> course;

  private void setCourseStr(String courseStr){
    this.courseStr = courseStr;
    try {
      this.course = new ObjectMapper().readValue(courseStr, Map.class);
    } catch (IOException e) {
      log.error("Failed to parse specificationsStr: {}", course, e);
    }
  }
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserCourse_CourseMap {
    private String name;        // 课程名称
    private String week;        // 周几
    private Integer[] num;          // 节次数组
    private String teacher;     // 教师姓名
    private String location;    // 教室位置
    private String color;       // 颜色代码（如：#4682B4）
  }

}

