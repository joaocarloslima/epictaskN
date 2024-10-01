package br.com.fiap.epictaskn.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public void create(Task task) {
        taskRepository.save(task);
    }

    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }
}
