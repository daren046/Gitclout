package fr.uge.gitclout.api;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to send progress updates to the client.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Singleton
@ServerWebSocket("/progress")
public class ProgressWebSocket {
  private static final Logger logger = LoggerFactory.getLogger(ProgressWebSocket.class);
  private final WebSocketBroadcaster broadcaster;


  /**
   * Constructor.
   * @param broadcaster The WebSocket broadcaster.
   */
  public ProgressWebSocket(WebSocketBroadcaster broadcaster) {
    this.broadcaster = broadcaster;
  }


  /**
   * This method is called when a new WebSocket connection is opened.
   * @param session The WebSocket session.
   */
  @OnOpen
  public void onOpen(WebSocketSession session) {
    logger.info("New WebSocket connection : " + session.getId());
  }


  /**
   * This method is called when a WebSocket connection is closed.
   * @param session The WebSocket session.
   */
  @OnClose
  public void onClose(WebSocketSession session) {
    logger.info("Connexion WebSocket closed : " + session.getId());
  }


  /**
   * This method is called when a WebSocket message is received.
   * @param message The WebSocket message.
   */
  @OnMessage
  public void onMessage(String message) {
    // You can keep this method empty if you don't need to handle incoming messages
    logger.info("Message received : " + message);
  }


  /**
   * This method is used to send a progress update to the client.
   * @param progress The progress of the tag.
   */
  public void sendProgressUpdate(int progress) {
    try {
      broadcaster.broadcastSync(progress);
    } catch (Exception e) {
      logger.error("Error occurred when sending the progression's update", e);
    }
  }


  /**
    * This method is used to send a progress update to the client.
    * @param tagProgress The progress of the tag.
    * @param tagSize The size of the tag.
   */
  public  void updateProgress(int tagProgress, int tagSize) {
    int progress;
    if (tagSize == 0) {
      progress = 100;
      sendProgressUpdate(progress);
      return;
    }
    progress = (int) ((tagProgress / (double) tagSize) * 100);
    sendProgressUpdate(progress);
  }
}