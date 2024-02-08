package leenwood.junior.test_task.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class VideoEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    private String videoPath;

    private String contentType;

    private String fileName;

    private boolean processing = false;

    private Boolean processingSuccess = null;


}
