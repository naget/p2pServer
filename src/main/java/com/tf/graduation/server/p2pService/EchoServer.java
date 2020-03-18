package com.tf.graduation.server.p2pService;

import com.tf.graduation.server.Model.ResponseModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.stereotype.Service;

@Service
public class EchoServer {
    
    public ResponseModel start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    b.group(group)
                            .channel(NioDatagramChannel.class)
                            .option(ChannelOption.SO_BROADCAST, true)
                            .handler(new EchoServerHandler());

                    b.bind(7402).sync().channel().closeFuture().await();
                } catch (Exception e) {
                    e.printStackTrace();
                    ResponseModel.fail(400,e.getMessage());
                } finally{
                    group.shutdownGracefully();
                }
            }
        }).start();

        return ResponseModel.success();
        
    }
}