package com.poudel.taskmaster.controller;
import com.poudel.taskmaster.model.History;
import com.poudel.taskmaster.model.Task;
import com.poudel.taskmaster.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v2")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;

    @GetMapping("/tasks")
    public List<Task> getTasks(){
        return (List) taskRepository.findAll();
    }

    @PostMapping("/tasks")
    public Task addNewTask(@RequestBody Task task) {
        Task newTask = new Task(task.getTitle(), task.getDescription(),
                task.getAssignee());
        History history = new History("Task is assigned to: " + task.getAssignee());
        newTask.addToHistory(history);
        taskRepository.save(newTask);
        return newTask;
    }

    @GetMapping("/users/{name}/tasks")
    public List<Task> getTasksofUser(@PathVariable String name){
        return (List) taskRepository.findAllByAssignee(name);
    }

    @PutMapping("/tasks/{id}/state")
    public Task updateStatus(@PathVariable String id){
        Task task = taskRepository.findById(id).get();
        String status = task.getStatus();

        if(status.equals("available")){
            task.setStatus("assigned");
            History history = new History("Task assigned to " + task.getAssignee());
            task.addToHistory(history);
        }else if(status.equals("assigned")){
            task.setStatus("accepted");
            History history = new History("Task accepted by " + task.getAssignee());
            task.addToHistory(history);
        }else if(status.equals("accepted")){
            task.setStatus("finished");
            History history = new History("Task finished by " + task.getAssignee());
            task.addToHistory(history);
        }
        taskRepository.save(task);
        return task;
    }

    @PutMapping("/tasks/{id}/assign/{assignee}")
    public Task updateAssignee(@PathVariable String id, @PathVariable String assignee) {
        Task task = taskRepository.findById(id).get();
        task.setAssignee(assignee);
        task.setStatus("assigned");
        String status = task.getStatus();
        History history = new History("Task assigned to: " + task.getAssignee());
        task.addToHistory(history);
        taskRepository.save(task);
        return task;
    }

}
