import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.VideoDataBase

class VideoMapper {
    fun toDatabase(apiVideo: Video, userId: Int): VideoDataBase {
        return VideoDataBase(
            videoId = apiVideo.id,
            title = apiVideo.title,
            url = apiVideo.url,
            duration = apiVideo.duration,
            userId = userId
        )
    }

    fun toApi(dbVideo: VideoDataBase): Video {
        return Video(
            id = dbVideo.videoId,
            title = dbVideo.title,
            url = dbVideo.url,
            duration = dbVideo.duration
        )
    }
}