package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    public UserDaoHibernateImpl() {
    }
    @Override
    public void createUsersTable() {
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            session
                    .createNativeQuery("""
            CREATE TABLE IF NOT EXISTS users.users (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(50) NOT NULL, 
            last_name VARCHAR(50) NOT NULL,
            age INT NOT NULL                     
            );
            """)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            session
                    .createNativeQuery("DROP TABLE IF EXISTS users.users")
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            User user = new User(name, lastName, age);
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, id);
            session.delete(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            Query<User> usersQueried = session.createQuery("from User", User.class);
            usersQueried.getResultStream().forEach(users::add);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = Util.getConfig().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE from User").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
