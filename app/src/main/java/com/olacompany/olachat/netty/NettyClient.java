package com.olacompany.olachat.netty;

import android.app.Activity;
import android.util.Log;

import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.friend.FriendActivity;
import com.olacompany.olachat.game.boomspin.BoomSpinActivity;
import com.olacompany.olachat.lobby.LobbyActivity;
import com.olacompany.olachat.login.LoginActivity;
import com.olacompany.olachat.room.ChatRoomActivity;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;

public class NettyClient {

    private static transient Channel session;
    private static long userId = -1;
    private static String userName = "", userMemo = "";
    private static int userProfileImageCode ,userWin, userLose, userPopularity;
    
    public NettyClient(Channel channel){
        this.session = channel;
    }

    public static Channel getSession() {
        return session;
    }

    public static void setSession(Channel session) {
        NettyClient.session = session;
    }

    public static void setUserInfo(LittleEndianReader r){
        NettyClient.userId = r.readLong();
        NettyClient.userName = r.readLengthAsciiString();
        NettyClient.userProfileImageCode = r.readInt();
        NettyClient.userWin = r.readInt();
        NettyClient.userLose = r.readInt();
        NettyClient.userPopularity = r.readInt();
        NettyClient.userMemo = r.readLengthAsciiString();
    }

    public static void setUserMemo(String userMemo) {
        NettyClient.userMemo = userMemo;
    }

    public static String getUserMemo() {
        return userMemo;
    }

    public static void setUserName(String userName) {
        NettyClient.userName = userName;
    }

    public static String getUserName() {
        return userName;
    }

    public static long getUserId() {
        if(userId == -1){
            Log.e("USERID ERR","not save userid");
        }
        return userId;
    }

    public static void setUserId(long userId) {
        NettyClient.userId = userId;
    }

    public static int getUserLose() {
        return userLose;
    }

    public static int getUserPopularity() {
        return userPopularity;
    }

    public static int getUserProfileImageCode() {
        return userProfileImageCode;
    }

    public static int getUserWin() {
        return userWin;
    }

    public static void setUserLose(int userLose) {
        NettyClient.userLose = userLose;
    }

    public static void setUserPopularity(int userPopularity) {
        NettyClient.userPopularity = userPopularity;
    }

    public static void setUserProfileImageCode(int userProfileImageCode) {
        NettyClient.userProfileImageCode = userProfileImageCode;
    }

    public static void setUserWin(int userWin) {
        NettyClient.userWin = userWin;
    }

    public static Activity getNowActivity(int code){
        switch (code){
            case 0:
                return LoginActivity.getInstance();
            case 1:
                return LobbyActivity.getInstance();
            case 2:
                return ChatRoomActivity.getInstance();
            case 3:
                return FriendActivity.getInstance();
            default:
                return null;
        }
    }

    public static Activity getNowActivity(String activityName){
        switch (activityName){
            case "login.LoginActivity":
                return LoginActivity.getInstance();
            case "lobby.LobbyActivity":
                return LobbyActivity.getInstance();
            case "room.ChatRoomActivity":
                return ChatRoomActivity.getInstance();
            case "friend.FriendActivity":
                return FriendActivity.getInstance();
            case "game.boomspin.BoomSpinActivity":
                return BoomSpinActivity.getInstance();
            default:
                return null;
        }
    }

    public static void initLoginServer(){
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new NettyDecoder());
                            p.addLast("encoder",new NettyEncoder());
                            p.addLast("handler",new NettyClientHandler(ClientStatus.LOGIN, 0));

                        }
                    });
            ChannelFuture f = b.connect("121.151.253.219",8484).sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void initChannelServer(int channel){
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new NettyDecoder());
                            p.addLast("encoder",new NettyEncoder());
                            p.addLast("handler",new NettyClientHandler(ClientStatus.CHANNEL, channel));

                        }
                    });
            ChannelFuture f = b.connect("121.151.253.219",8485).sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


}
