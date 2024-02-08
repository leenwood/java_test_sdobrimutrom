package leenwood.junior.test_task.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class VideoStatusResponse {

    private UUID id;

    private String filename;

    private boolean processing;

    private Boolean processingSuccess;


}
