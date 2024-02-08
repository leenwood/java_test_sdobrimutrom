package leenwood.junior.test_task.repository;

import leenwood.junior.test_task.entity.VideoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VideoEntityRepository extends CrudRepository<VideoEntity, UUID> {

    public VideoEntity findOneVideoPathById(UUID id);

}
