package leenwood.junior.test_task.controller;


import leenwood.junior.test_task.model.CropVideoRequest;
import leenwood.junior.test_task.model.ExceptionModelResponse;
import leenwood.junior.test_task.model.SuccessResponse;
import leenwood.junior.test_task.model.VideoStatusResponse;
import leenwood.junior.test_task.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequestMapping("/file/")
@RestController
public class VideoController {


    private final VideoService uploadVideoService;


    @Autowired
    public VideoController(VideoService uploadVideoService) {
        this.uploadVideoService = uploadVideoService;
    }


    @PostMapping
    public Map<String, Object> uploadVideo(@RequestParam("video") MultipartFile video) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if (video.isEmpty() || !Objects.equals(video.getContentType(), "video/mp4")) {
            response.put("success", false);
            return response;
        }

        UUID uuid = this.uploadVideoService.UploadVideo(video);

        if (uuid == null) {
            response.put("success", false);
            return response;
        }

        response.put("success", true);
        response.put("id", uuid.toString());

        return response;
    }

    @PatchMapping("/{id}")
    public SuccessResponse cropVideo(
            @RequestBody CropVideoRequest request,
            @PathVariable UUID id
    ) {

        boolean answer = this.uploadVideoService.CropVideo(request.getWidth(), request.getHeight(), id);

        return new SuccessResponse(answer);
    }

    @GetMapping("/{id}")
    public VideoStatusResponse getVideoStatus(@PathVariable UUID id) {
        return this.uploadVideoService.getVideoStatus(id);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteVideo(@PathVariable UUID id) {
        return new SuccessResponse(this.uploadVideoService.deleteVideo(id));
    }


    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionModelResponse exceptionListener(Throwable exception) {
        return new ExceptionModelResponse(exception.getMessage());
    }
}
