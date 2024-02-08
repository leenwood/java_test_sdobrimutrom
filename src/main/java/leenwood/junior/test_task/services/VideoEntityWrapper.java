package leenwood.junior.test_task.services;

import leenwood.junior.test_task.entity.VideoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoEntityWrapper {
    private VideoEntity videoEntity;

    public VideoEntityWrapper(VideoEntity videoEntity) {
        this.videoEntity = videoEntity;
    }

}
