package com.tf.graduation.server.p2pService;

import com.alibaba.fastjson.JSONObject;
import com.tf.graduation.server.Model.UserInfoOnLine;
import com.tf.graduation.server.service.RedisService;
import com.tf.graduation.server.service.UserServiceImpl;
import com.tf.graduation.server.utils.JavaWebToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EchoServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static UserServiceImpl userService;
    private static RedisService redisService;

    static {
        userService = ToolNettySpirngAutowired.getBean(UserServiceImpl.class);
        redisService = ToolNettySpirngAutowired.getBean(RedisService.class);
    }

    boolean flag = false;
    Map<String,InetSocketAddress> addr1 = new ConcurrentHashMap<>();
    Map<String,List<InetSocketAddress>> addr2 = new ConcurrentHashMap<>();

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
        } catch (Exception e) {
            throw new RuntimeException("message解析json格式出错：" + e.getMessage());
        }
        if (message.getString("token") == null) {
            log.info("缺少token，请求被忽略");
            return;
        }

//            UserInfoOnLine userInfoOnLine = redisService.getUserInfo(message.getString("token"));
        Map<String, Object> info = JavaWebToken.parserJavaWebToken(message.getString("token"));
        log.info("p2pService通过token拿到信息："+info);
        String user;
        String macAddress;
        if (info==null){
            log.info("info为null,请求被忽略");
            return;
        }else {
            user = info.get("userId").toString();
            macAddress = info.get("macAddress").toString();
            if (user==null||macAddress==null){
                log.info("token中缺少参数 user:"+user+"macAddress:"+macAddress);
                return;
            }
        }

        NettySocketMap.put(user+macAddress, user, packet.sender());
        if (message.getString("type").equals("H")){
            log.info("收到客户端心跳:"+packet.sender());
        }
        else if (message.get("type").equals("B")) {
            log.info("B指令:"+packet.sender());
        } else if (message.get("type").equals("L")) {
            //保存到addr1中 并发送addr2
            addr1.put(user,packet.sender());
            System.out.println("L 命令， 保存到addr1中 ");
            log.info("对L指令回复到：" + addr1.get(user));
            JSONObject remote = new JSONObject();
            if (addr2.get(user)!=null){
                addr2.get(user).clear();

            }
            Set<String> ids = NettySocketMap.getGroup(user);
            for (String id :
                    ids) {
                InetSocketAddress address = NettySocketMap.get(id);
                log.info("遍历到addresss中的address：" + address);
                if (message.getString("version")!=null){
                    remote.put("version",message.getString("version"));
                }
                if (!id.equals(user+macAddress)){
                    log.info("获取R地址:"+address);
                    remote.put("type", "AL");
                    remote.put("ip", address.getAddress().toString().replace("/", ""));
                    remote.put("port", address.getPort());
                    ctx.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(remote.toJSONString().getBytes()), packet.sender()));
                    log.info("发送"+remote+"给"+packet.sender());
                    remote.put("type","AR");
                    remote.put("ip",packet.sender().getAddress().toString().replace("/",""));
                    remote.put("port",packet.sender().getPort());
                    ctx.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(remote.toJSONString().getBytes()), address));
                    log.info("发送"+remote+"给"+address);
//                    break;
                }
            }
        } else if (message.get("type").equals("R")) {
            //保存到addr2中 并发送addr1
            List<InetSocketAddress>  socketAddresses = addr2.computeIfAbsent(user,k->new ArrayList<InetSocketAddress>());
            socketAddresses.add(packet.sender());
            log.info("R 命令， 地址保存到addr2列表中 :"+packet.sender());
        } else if (message.get("type").equals("M")) {
            //addr1 -> addr2
            JSONObject remote = new JSONObject();
            if (message.get("version") != null) {
                remote.put("version", message.getString("version"));
            }
            if (addr2.get(user)!=null&&addr2.get(user).size()>0){
                for (int i=0;i<addr2.get(user).size();i++){
                    InetSocketAddress address = addr2.get(user).get(i);
                    remote.put("type", "AL");
                    remote.put("ip", address.getAddress().toString().replace("/", ""));
                    remote.put("port", address.getPort());

//            String remot = "A " + addr2.getAddress().toString().replace("/", "")
//                    +" "+addr2.getPort();
                    ctx.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(remote.toJSONString().getBytes()), addr1.get(user)));
                    //addr2 -> addr1
//            remot = "A " + addr1.getAddress().toString().replace("/", "")
//                    +" "+addr1.getPort();
                    log.info("发送信息:" + remote.toJSONString() + "给" + addr1.get(user));
                    remote.put("type", "AR");
                    remote.put("ip", addr1.get(user).getAddress().toString().replace("/", ""));
                    remote.put("port", addr1.get(user).getPort());
                    ctx.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(remote.toJSONString().getBytes()), address));
                    log.info("发送信息:" + remote.toJSONString() + "给" + address);
                    System.out.println("M 命令");
                }
            }else {
                log.info("服务器收到M指令，但是没有收到可以同步的远程结点的响应");
            }
        }
        System.out.println("收到消息：" + str);
        buf.release();

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器启动...");

        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.READER_IDLE.equals(event.state())) { // 如果读通道处于空闲状态，说明没有接收到心跳命令
                log.info("已等待15秒还没收到客户端发来的消息");
//                NettySocketMap.remove(ctx.channel().);
                // TODO: 2020/4/10 移除map中的对应元素
                ctx.channel().close();

            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }
}