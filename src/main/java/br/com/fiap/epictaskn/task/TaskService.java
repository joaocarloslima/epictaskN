package br.com.fiap.epictaskn.task;

import br.com.fiap.epictaskn.user.User;
import br.com.fiap.epictaskn.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public void create(Task task) {
        taskRepository.save(task);
    }

    public void delete(UUID id) {
        var task = getTask(id);
        if (task.getUser() != null){
            throw new RuntimeException("Não pode apagar tarefa atribuída");
        }
        taskRepository.deleteById(id);
    }

    public void catchTask(UUID id, User user) {
        var task = getTask(id);
        if(task.getUser() != null) {
            throw new RuntimeException("Tarefa já atribuída");
        }
        task.setUser(user);
        taskRepository.save(task);
    }

    public void releaseTask(UUID id, User user) {
        var task = getTask(id);
        if (!task.getUser().equals(user)){
            throw new RuntimeException("Tarefa atribuída para outro usuário");
        }
        task.setUser(null);
        taskRepository.save(task);
    }

    public void decTask(UUID id, User user) {
        var task = getTask(id);
        if(task.getStatus() - 10 < 0) return;
        task.setStatus(task.getStatus() - 10);
        taskRepository.save(task);
    }

    public void incTask(UUID id, User user) {
        var task = getTask(id);
        if(task.getStatus() + 10 > 100) return;
        task.setStatus(task.getStatus() + 10);
        if (task.getStatus() == 100){
            userService.addScore(user, task.getScore());
        }
        taskRepository.save(task);
    }

    private Task getTask(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
    }


    public List<Task> findPeding() {
        return taskRepository.findByStatusLessThan(100);
    }
}
