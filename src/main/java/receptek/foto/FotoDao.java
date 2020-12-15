package receptek.foto;

import util.jpa.GenericJpaDao;

import javax.persistence.Persistence;
import java.util.List;

public class FotoDao extends GenericJpaDao<Foto> {
    private static FotoDao instance;

    private FotoDao(){
        super(Foto.class);
    }

    public static FotoDao getInstance(){
        if(instance == null){
            instance = new FotoDao();
            instance.setEntityManager(
                    Persistence
                            .createEntityManagerFactory("jpa-persistence-unit-1")
                            .createEntityManager()
            );
        }
        return instance;
    }
}
