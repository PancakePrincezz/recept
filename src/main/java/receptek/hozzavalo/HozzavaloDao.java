package receptek.hozzavalo;

import util.jpa.GenericJpaDao;

import javax.persistence.Persistence;
import java.util.List;

public class HozzavaloDao extends GenericJpaDao<Hozzavalo> {
    private static HozzavaloDao instance;

    private HozzavaloDao(){
        super(Hozzavalo.class);
    }

    public static HozzavaloDao getInstance(){
        if(instance == null){
            instance = new HozzavaloDao();
            instance.setEntityManager(
                    Persistence
                            .createEntityManagerFactory("jpa-persistence-unit-1")
                            .createEntityManager()
            );
        }
        return instance;
    }
}
