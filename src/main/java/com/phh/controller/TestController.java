package com.phh.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author phh
 * @version V1.0
 * @date 2019/10/17
 */
@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * 流程布署
     */
    @ResponseBody
    @RequestMapping("/deploy")
    public void deploy() {
        DeploymentBuilder builder = repositoryService.createDeployment();
        // bpmn文件的名称
        builder.addClasspathResource("processes/leave.bpmn");
        builder.addClasspathResource("processes/leave.png");
        // 设置key
        builder.key("leave");
        // 设定名称，也可以在图中定义
        builder.name("请假流程");
        // 进行布署
        Deployment deployment = builder.deploy();
        log.info("部署ID：" + deployment.getId());
        log.info("部署名称：" + deployment.getName());
    }

    /**
     * 流程启动
     */
    @ResponseBody
    @RequestMapping("/start")
    public void start() {
        // 每一个流程有对应的一个key这个是某一个流程内固定的写在bpmn内的
        String processDefinitionKey = "leave";
        HashMap<String, Object> variables = new HashMap<>();
        // 流程实例
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKey(processDefinitionKey, variables);

        log.info("流程实例ID:" + instance.getId());
        log.info("流程定义ID:" + instance.getProcessDefinitionId());
    }

    /**
     * 查询当前人的个人任务
     * 查询任务表ACT_RU_TASK。启动实例之后，实例直接运转到请假申请节点。
     */
    @ResponseBody
    @RequestMapping("/findTask")
    public void findTask() {
        // 创建任务查询对象
        List<Task> list = taskService.createTaskQuery().list();
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                log.info("任务ID:" + task.getId());
                log.info("任务名称:" + task.getName());
                log.info("任务的创建时间:" + task.getCreateTime());
                log.info("任务的办理人:" + task.getAssignee());
                log.info("流程实例ID：" + task.getProcessInstanceId());
                log.info("执行对象ID:" + task.getExecutionId());
                log.info("流程定义ID:" + task.getProcessDefinitionId());
            }
        }
    }

    /**
     * 完成用户任务
     * 完成该任务之后，实例直接运转到了总经理审批节点，完成任务的时候如果没有传递day参数，
     * 会报错。继续查询待办-完成任务，直至实例结束即可。
     */
    @ResponseBody
    @RequestMapping("/completeTask")
    public void completeTask() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("day", 4);
        //按配置的任务id填写
        map.put("_7", "true");
        //参数为：act_ru_task 表中的ID_
        taskService.complete("10005", map);
    }

}
