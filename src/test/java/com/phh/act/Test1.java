package com.phh.act;

import com.phh.ActivitiExampleApp;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author phh
 * @version V1.0
 * @date 2020/3/9
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivitiExampleApp.class})
public class Test1 {

    private static final Logger log = LoggerFactory.getLogger(Test1.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 流程部署
     */
    @Test
    public void testDeploy() {
        DeploymentBuilder builder = repositoryService.createDeployment();
        // bpmn文件的名称
        builder.addClasspathResource("processes/leave.bpmn");
        builder.addClasspathResource("processes/leave.png");
        // 设置key
        builder.key("leave2");
        // 设定名称，也可以在图中定义
        builder.name("请假流程");
        // 进行布署
        Deployment deployment = builder.deploy();
        log.info("部署ID：" + deployment.getId());
        log.info("部署名称：" + deployment.getName());
    }

    /**
     * 删除流程定义
     */
    @Test
    public void testDelete() {
        repositoryService.deleteDeployment("1", true);
    }

    /**
     * 启动流程实列
     */
    @Test
    public void testStart() {
        //定义流程变量
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("day", "5");
        variables.put("leaveUser", "张三");
        variables.put("dm", "李经理");
        variables.put("gm", "王总经理");
        String businesskey = "A002";
        //第一个参数流程图key，第二个参数businesskey，第三个参数流程变量
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave", businesskey, variables);
        System.out.println("实列ID：" + processInstance.getId());
        System.out.println("部署ID：" + processInstance.getDeploymentId());
        System.out.println("actID：" + processInstance.getActivityId());
    }

    /**
     * 查看任务
     */
    @Test
    public void testQueryTask() {
        //任务办理人
        String assignee = "李经理";
        List<Task> list = taskService.createTaskQuery()//
                .taskAssignee(assignee)//个人任务的查询
                .list();
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务ID：" + task.getId());
                System.out.println("任务的办理人：" + task.getAssignee());
                System.out.println("任务名称：" + task.getName());
                System.out.println("任务的创建时间：" + task.getCreateTime());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("#######################################");
            }
        }
    }

    @Test
    public void testQueryHistoryTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId("7501").list();
        if (list != null && list.size() > 0) {
            for (HistoricTaskInstance hti : list) {
                System.out.println(hti.getId() + "    " + hti.getName() + "   " + hti.getClaimTime());
            }
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask() {
        //任务ID
        String taskId = "7513";
        taskService.complete(taskId);
        System.out.println("完成任务：" + taskId);
    }

}
