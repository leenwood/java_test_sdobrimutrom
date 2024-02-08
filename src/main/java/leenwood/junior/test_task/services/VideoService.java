package leenwood.junior.test_task.services;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import leenwood.junior.test_task.entity.VideoEntity;
import leenwood.junior.test_task.model.VideoStatusResponse;
import leenwood.junior.test_task.repository.VideoEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

@Service
public class VideoService {


    private final VideoEntityRepository videoPathRepository;


    @Autowired
    public VideoService(VideoEntityRepository videoPathRepository) {
        this.videoPathRepository = videoPathRepository;
    }


    public UUID UploadVideo(MultipartFile video) throws IOException {

        String uploadDir = "./uploads/videos/";
        Path uploadPath = Paths.get(uploadDir);

        VideoEntity videoEntity = new VideoEntity();

        Path filePath = uploadPath.resolve(String.valueOf(Instant.now().getEpochSecond()) + ".mp4");
        videoEntity.setFileName(video.getOriginalFilename());
        videoEntity.setVideoPath(filePath.toString());
        videoEntity.setContentType(video.getContentType());

        this.videoPathRepository.save(videoEntity);

        Files.copy(video.getInputStream(), filePath);

        return videoEntity.getId();
    }


    public VideoStatusResponse getVideoStatus(UUID id) {
        VideoEntity videoEntity = this.videoPathRepository.findOneVideoPathById(id);
        return new VideoStatusResponse(
                videoEntity.getId(),
                videoEntity.getFileName(),
                videoEntity.isProcessing(),
                videoEntity.getProcessingSuccess()
        );
    }


    public boolean CropVideo(int width, int height, UUID id) {
        VideoEntity videoEntity = this.videoPathRepository.findOneVideoPathById(id);
        VideoEntityWrapper videoPathWrapper = new VideoEntityWrapper(videoEntity);


        String[] commands = {
                "ffmpeg",
                "-i", videoEntity.getVideoPath(),
                "-vf", "scale=" + width + ":" + height,
                "-c:a", "copy",
                videoEntity.getVideoPath()
        };



        videoEntity.setProcessing(true);
        Observable.fromCallable(() -> executeFFmpeg(commands))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(exitCode -> {
                    VideoEntity updateVideoEntity = videoPathWrapper.getVideoEntity();
                    if (exitCode == 0 ) {
                        updateVideoEntity.setProcessingSuccess(true);
                        updateVideoEntity.setProcessing(false);
                    } else {
                        updateVideoEntity.setProcessingSuccess(false);
                        updateVideoEntity.setProcessing(false);
                    }
                    this.videoPathRepository.save(updateVideoEntity);
                }, Throwable::printStackTrace);

        return true;
    }

    private int executeFFmpeg(String[] commands) {

        int answer = -1;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            Process process = processBuilder.start();
            answer = process.waitFor();
        } catch (InterruptedException | IOException exception) {
            System.out.println(exception.getMessage());
        }

        return answer;
    }

    public boolean deleteVideo(UUID id) {
        VideoEntity videoEntity = this.videoPathRepository.findOneVideoPathById(id);
        File file = new File(videoEntity.getVideoPath());

        if (file.delete()) {
            this.videoPathRepository.delete(videoEntity);
            return true;
        } else {
            return false;
        }
    }
}
