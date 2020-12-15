package receptek.recept;

import util.jpa.GenericJpaDao;

import javax.persistence.Persistence;
import java.util.List;

public class ReceptDao extends GenericJpaDao<Recept> {
    private static ReceptDao instance;

    private ReceptDao(){
        super(Recept.class);
    }

    public static ReceptDao getInstance(){
        if(instance == null){
            instance = new ReceptDao();
            instance.setEntityManager(
                    Persistence
                            .createEntityManagerFactory("jpa-persistence-unit-1")
                            .createEntityManager()
            );
        }
        return instance;
    }
}
