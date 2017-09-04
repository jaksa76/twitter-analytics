package com.zuhlke.ta.twitterclient;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import twitter4j.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by eabi on 04/09/2017.
 */
public class ListenerTest {
    @Test
    public void shouldSendTweetToSinkWhenNewStatusReceived() {

        Instant instant = Instant.now();

        long expectedId = 5;
        String expectedUser = "jack";
        String expectedMessage = "Hello, World!";
        LocalDate expectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        TweetSink sink = mock(TweetSink.class);

        Listener target = new Listener(sink);

        target.onStatus(createTestStatus(expectedId, expectedUser, expectedMessage, Date.from(instant)));

        verify(sink).addTweet(
                argThat(t -> t.id == expectedId && t.message.equals(expectedMessage) && t.userId.equals(expectedUser) && t.date.equals(expectedDate)));
    }

    private Status createTestStatus(long id, String userScreenName, String text, Date createdAt) {
        User user = mock(User.class);
        when(user.getScreenName()).thenReturn(userScreenName);

        Status status = mock(Status.class);
        when(status.getId()).thenReturn(id);
        when(status.getText()).thenReturn(text);
        when(status.getCreatedAt()).thenReturn(createdAt);
        when(status.getUser()).thenReturn(user);

        return status;
    }
}
