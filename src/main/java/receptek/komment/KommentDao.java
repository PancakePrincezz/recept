package receptek.komment;

import receptek.user.User;
import util.jpa.GenericJpaDao;

import javax.persistence.Persistence;
import java.util.List;

public class KommentDao extends GenericJpaDao<Komment> {
    private static KommentDao instance;

    private KommentDao(){
        super(Komment.class);
    }

    public static KommentDao getInstance(){
        if(instance == null){
            instance = new KommentDao();
            instance.setEntityManager(
                    Persistence
                            .createEntityManagerFactory("jpa-persistence-unit-1")
                            .createEntityManager()
            );
        }
        return instance;
    }

    public List<Komment> getByReceptId(int receptId){
        return entityManager
                .createQuery("SELECT k FROM Komment k WHERE k.ReceptId = :receptId", Komment.class)
                .setParameter("receptId", receptId)
                .getResultList();
    }
}
