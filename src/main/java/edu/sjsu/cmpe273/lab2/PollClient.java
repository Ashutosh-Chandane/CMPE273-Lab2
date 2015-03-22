package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class PollClient {
  private static final Logger logger = Logger.getLogger(PollClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

  public PollClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void createPoll() {
    try {
      logger.info("Will try to create poll ...");
 //     List<String> lst = new ArrayList<String>();
 //     lst.add("5");
 //     Iterator<String> itr = lst.iterator();

       String[] choice = {"Android","iPhone"};

      PollRequest request = PollRequest.newBuilder().setModeratorId("1").setQuestion("What type of smartphone do you have?").setStartedAt("2015-02-23T13:00:00.000Z").setExpiredAt("2015-02-24T13:00:00.000Z").addChoice(choice[0]).addChoice(choice[1]).build();
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Created a new poll with id = " + response.getId());
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    PollClient client = new PollClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String user = "world";
      if (args.length > 0) {
        user = args[0]; /* Use the arg as the name to greet if provided */
      }
      client.createPoll();
    } finally {
      client.shutdown();
    }
  }
}