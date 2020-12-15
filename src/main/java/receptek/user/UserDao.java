package receptek.user;

import util.jpa.GenericJpaDao;

import javax.persistence.Persistence;
import java.util.List;

public class UserDao extends GenericJpaDao<User> {
    private static UserDao instance;

    private UserDao(){
        super(User.class);
    }

    public static UserDao getInstance(){
        if(instance == null){
            instance = new UserDao();
            instance.setEntityManager(
                    Persistence
                            .createEntityManagerFactory("jpa-persistence-unit-1")
                    .createEntityManager()
            );
        }
        return instance;
    }

    public List<User> getByUserId(int userId){
        return entityManager
                .createQuery("SELECT u FROM User u WHERE u.UserId = :id", User.class)
                .setParameter("id", userId)
                .getResultList();
    }

    public List<User> getUserByName(String userName){
        return entityManager
                .createQuery("SELECT u FROM User u WHERE u.UserName = :userName", User.class)
                .setParameter("userName", userName)
                .getResultList();
    }

    public List<User> getUserByNameAndPasswordHash(String userName, String pwHash){
        return entityManager
                .createQuery("SELECT u FROM User u WHERE u.UserName = :userName AND u.PwHash = :pwHash", User.class)
                .setParameter("userName", userName)
                .setParameter("pwHash", pwHash)
                .getResultList();
    }
}
