package rmkbot.streamlive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import rmkbot.SetupBot;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LiveChatReader {

    public static YouTube yt;



    public static void main(String[] args) {
        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE);

        try {
            Credential credential = SetupBot.authorize(scopes,"readlivechat");

            String liveChat = args.length == 1
                    ? readLiveChat(yt, args[0])
                    : readLiveChat(yt);

            if(liveChat !=null){
                System.out.println("Live chat id: " + liveChat);
            } else {
                System.err.println("Unable to find a live chat id");
                System.exit(1);
            }
        } catch (GoogleJsonResponseException e) {
            System.err
                    .println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
}

    static String readLiveChat(YouTube youtube) throws IOException {
        // Get signed in user's liveChatId
        YouTube.LiveBroadcasts.List broadcastList = youtube
                .liveBroadcasts()
                .list("snippet")
                .setFields("items/snippet/liveChatId")
                .setBroadcastType("all")
                .setBroadcastStatus("active");
        LiveBroadcastListResponse broadcastListResponse = broadcastList.execute();
        for (LiveBroadcast b : broadcastListResponse.getItems()) {
            String liveChatId = b.getSnippet().getLiveChatId();
            if (liveChatId != null && !liveChatId.isEmpty()) {
                return liveChatId;
            }
        }

        return null;
    }


    static String readLiveChat(YouTube youtube, String videoId) throws IOException {
        // Get liveChatId from the video
        YouTube.Videos.List videoList = youtube.videos()
                .list("liveStreamingDetails")
                .setFields("items/liveStreamingDetails/activeLiveChatId")
                .setId(videoId);
        VideoListResponse response = videoList.execute();
        for (Video v : response.getItems()) {
            String liveChatId = v.getLiveStreamingDetails().getActiveLiveChatId();
            if (liveChatId != null && !liveChatId.isEmpty()) {
                return liveChatId;
            }
        }

        return null;
    }
}
