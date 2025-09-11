package com.example.aipartner.controller;

import com.example.aipartner.pojo.IndividualPlaning.UserCourse;
import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.service.IndividualStudyPlanningService;
import com.example.aipartner.utils.AliOSSUtils;
import com.example.aipartner.utils.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * 个人学习计划
 */
@Slf4j
@RestController
public class IndividualStudyPlanningController {

    @Autowired
    private IndividualStudyPlanningService individualStudyPlanningService;

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 创建个人学习计划
     * @param request
     * @return
     */
    @PostMapping("/IndividualPlaning/Create")
//    public Result Create(@RequestBody Map<String, List<Map<String,Object>>> request) {
    public Result Create(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.Create(request,map);
    }

    /**
     * 获取个人学习计划包括知识点（写错了补药管这个）
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/listAll")
    public Result GetLearnPlaning(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.GetLearnPlaning(map);
    }

    /**
     * 获取个人学习计划
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/listLearnPlaning")
    public Result listLearnPlaning(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listLearnPlaning(map);
    }

    /**
     * 获取个人学习计划下的知识点s
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/listKnowledgePoints")
    public Result listKnowledgePoints(@RequestBody Map<String, Long> request,HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listKnowledgePoints(request,map);
    }

    /**
     * 更新个人学习计划进度
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/updateProgressOfTheLearningPath")
    public Result updateProgressOfTheLearningPath(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.updateProgressOfTheLearningPath(request,map);
    }

    /**
     * 课表文件图片生成课表json
     * (双后端联动，端口8084)
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/IndividualPlaning/create")
    public Result uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.uploadFile(file,map,token);
    }

    /**
     * 获取课表
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/IndividualPlaning/getCourse")
    public Result getCourse(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
        return individualStudyPlanningService.listCourse(map);
    }

    /**
     * 添加课表
     * @param request
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/IndividualPlaning/AddCourse")
    public Result AddCourse(@RequestBody Map<String,List<UserCourse.UserCourse_CourseMap>> request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        Map<String, String> map = JWTUtils.getTokenInfo(token);
//        return Result.success(request);
        return individualStudyPlanningService.AddCourse(request,map);
    }







}
