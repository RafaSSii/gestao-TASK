package br.com.rafael.taskmanager.dao;

import br.com.rafael.taskmanager.model.Task;
import br.com.rafael.taskmanager.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public void save(Task task) {

        String sql = """
                INSERT INTO tasks (title, description, status, priority, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus());
            stmt.setString(4, task.getPriority());
            stmt.setTimestamp(5, Timestamp.valueOf(task.getCreatedAt()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> findAll() {

        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Task task = new Task(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("priority")
                );

                task.setId(rs.getInt("id"));
                task.setCreatedAt(
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public void update(Task task) {

        String sql = """
                UPDATE tasks
                SET title = ?, description = ?, status = ?, priority = ?
                WHERE id = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus());
            stmt.setString(4, task.getPriority());
            stmt.setInt(5, task.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countAll() {
        return countByQuery("SELECT COUNT(*) FROM tasks");
    }

    public int countByStatus(String status) {
        return countByQuery(
                "SELECT COUNT(*) FROM tasks WHERE status = '" + status + "'"
        );
    }

    private int countByQuery(String sql) {

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
