package semillero.ecosistema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semillero.ecosistema.entity.PublicationEntity;
import semillero.ecosistema.entity.UserEntity;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, String> {
    PublicationEntity findByTitleOrderByDateDesc(String title);
    List<PublicationEntity> findByUsuarioCreadorIdOrderByDateDesc(String user_id);
    List<PublicationEntity> findByHiddenFalseOrderByDateDesc();

    List<PublicationEntity> findAllByOrderByDateDesc();

}
