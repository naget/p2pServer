package com.tf.graduation.server.p2pService;

import com.alibaba.fastjson.JSONObject;
import com.tf.graduation.server.service.UserServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import sun.rmi.runtime.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class EchoServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static UserServiceImpl userService;

    static {
        userService = ToolNettySpirngAutowired.getBean(UserServiceImpl.class);
    }

    boolean flag = false;
    InetSocketAddress addr1 = null;
    InetSocketAddress addr2 = null;
    /**
     * channelRead0 是对每个发送过来的UDP包进行处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        ByteBuf buf = (ByteBuf) packet.copy().content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String str = new String(req, "UTF-8");
        JSONObject message;
        try {
            message = JSONObject.parseObject(str);
        }catch (Exception e){
            throw new RuntimeException("message解析json格式出错："+e.getMessage());
        }
        String user = message.getString("user");
        NettySocketMap.put(ctx.channel().id().asLongText(),user, packet.sender());
        if (message.get("type").equals("B")){
            log.info("B指令");
        }
        else if(message.get("type").equals("L")){
            //保存到addr1中 并发送addr2
            addr1 = packet.sender();
            System.out.println("L 命令， 保存到addr1中 ");
            log.info("对L指令回复到："+addr1);
            JSONObject MM = new JSONObject();
            MM.put("type","G");
            List<InetSocketAddress> addresss =NettySocketMap.getGroup(message.getString("user"));
            for (int i = 0;i<addresss.size();i++){
                InetSocketAddress address = addresss.get(i);
                log.info("遍历到addresss中的address："+address);
                if (!packet.sender().equals(addresss.get(i))){

                    log.info("通过"+address+"获取R地址");
                    ctx.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(MM.toJSONString().getBytes()),address));
                    break;
                }
            }
            Thread.sleep(2000);

            MM.put("type","MM");
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(MM.toJSONString().getBytes()), addr1));
            log.info("发送MM指令到："+addr1);
        }else if(message.get("type").equals("R")){
            //保存到addr2中 并发送addr1
            addr2 = packet.sender();
            System.out.println("R 命令， 保存到addr2中 ");
        }else if(message.get("type").equals("M")){
            //addr1 -> addr2
            JSONObject remote = new JSONObject();
            remote.put("type","A");
            remote.put("ip",addr2.getAddress().toString().replace("/", ""));
            remote.put("port",addr2.getPort());

//            String remot = "A " + addr2.getAddress().toString().replace("/", "")
//                    +" "+addr2.getPort();
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(remote.toJSONString().getBytes()), addr1));
            //addr2 -> addr1
//            remot = "A " + addr1.getAddress().toString().replace("/", "")
//                    +" "+addr1.getPort();
            log.info("发送信息:"+remote.toJSONString()+"给"+addr1.toString());
            remote.put("type","A");
            remote.put("ip",addr1.getAddress().toString().replace("/", ""));
            remote.put("port",addr1.getPort());
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(remote.toJSONString().getBytes()), addr2));
            log.info("发送信息:"+remote.toJSONString()+"给"+addr2.toString());
            System.out.println("M 命令");
        }
        System.out.println("收到消息："+str);
        
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器启动...");

        super.channelActive(ctx);
    }
}