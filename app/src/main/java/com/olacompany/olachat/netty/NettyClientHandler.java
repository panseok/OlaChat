package com.olacompany.olachat.netty;

import android.app.Activity;

import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.friend.FriendActivity;
import com.olacompany.olachat.game.boomspin.BoomSpinActivity;
import com.olacompany.olachat.lobby.LobbyActivity;
import com.olacompany.olachat.lobby.LobbyPagerAdapter;
import com.olacompany.olachat.login.LoadingActivity;
import com.olacompany.olachat.login.LoginActivity;
import com.olacompany.olachat.opcode.ReceiveOpcode;
import com.olacompany.olachat.packet.LoginPacket;
import com.olacompany.olachat.room.ChatRoomActivity;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ClientStatus clientStatus;
    private int channel;

    public NettyClientHandler(ClientStatus status, int channel){
        this.clientStatus = status;
        this.channel = channel;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if(clientStatus == ClientStatus.LOGIN){
            if(NettyClient.getSession() != null){ // channel > login
                NettyClient.getSession().close();
            }
            NettyClient.setSession(ctx.channel());
        }else if(clientStatus == ClientStatus.CHANNEL){
            NettyClient.getSession().close();
            NettyClient.setSession(ctx.channel());
            NettyClient.getSession().writeAndFlush(LoginPacket.sendConnectChannel(NettyClient.getUserId()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) {
        LittleEndianReader r = (LittleEndianReader) packet;

        final int opcode = r.readShort();

        for(ReceiveOpcode recv : ReceiveOpcode.values()){
            if(recv.ordinal() == opcode){
                try {
                    System.out.println("DEBUG "+recv.name()+"  "+r.toString());
                    handlePacket(r,recv);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }

/*
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //5.수신된 데이터를 모두 읽었을때 호출되는 이벤트 메서드
        ctx.close();//6.서버와 연결된 채널을 닫음
        //6.1 이후 데이터 송수신 채널은 닫히게 되고 클라이언트 프로그램은 종료됨
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        LoadingActivity.setConnected(false);
        if(clientStatus == ClientStatus.CHANNEL){
            OlaChat.ServerNoticeAlertDialog();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // 채널 비활성
        if(clientStatus == ClientStatus.CHANNEL){
            if(LoadingActivity.isConnected()){ //로그아웃 시에는 출력 X
                OlaChat.ServerNoticeAlertDialog();
            }
        }
    }


    private static final void handlePacket(LittleEndianReader r , ReceiveOpcode recv){

        switch (recv){
            case SERVER_PING:{
                NettyClient.getSession().writeAndFlush(LoginPacket.sendPong());
                break;
            }

            case SERVER_NOTICE:{
                LobbyActivity.getInstance().getServerNoticeList(r);
                break;
            }
            case LOGIN_STATUS_MSG:{
                LoginActivity.showLoginStatusMsg(r);
                break;
            }
            case LOGIN_CREATE_NAME:{
                LoginActivity.CreateUserNameAlertDialog();
                break;
            }
            case CONNECT_LOGIN_SERVER_ONLINE:{
                LoadingActivity.setConnected(true);
                break;
            }
            case CONNECT_CHANNEL_SERVER:{
                DevTools.startProgressDialogHandleLooper(LoginActivity.getInstance());
                NettyClient.initChannelServer(1);
                break;
            }
            case CONNECT_CHANNEL_SERVER_ONLINE:{
                NettyClient.setUserInfo(r);
                LoginActivity.getInstance().startLobbyActivity();
                break;
            }

            case USER_CHANGE_PROFILE_IMAGE_RESULT:{
                LobbyPagerAdapter.getInstance().getChangeProfileImageResult(r);
                break;
            }

            case USER_CHANGE_MEMO_RESULT:{
                LobbyPagerAdapter.getInstance().getChangeUserMemoResult(r);
                break;
            }

            case USER_GET_USER_INFO:{
                break;
            }

            case USER_OPEN_PROFILE:{
                UserInfo.ProfileAlertDialog(r);
                break;
            }

            case LOBBY_ROOM_LIST: {
                LobbyActivity.getInstance().getLobbyRoomListPacket(r);
                break;
            }

            case ROOM_JOIN_REQUEST_RESULT:{
                LobbyActivity.getInstance().getUserJoinRoomRequestResult(r);
                break;
            }

            case ROOM_SEND_NOTICE:{
                ChatRoomActivity.getInstance().readNotice(r);
                break;
            }

            case ROOM_SEND_CHAT:{
                ChatRoomActivity.getInstance().readChat(r);
                break;
            }

            case ROOM_USER_LIST:{
                ChatRoomActivity.getInstance().readUserList(r);
                break;
            }

            case ROOM_EXIT_BAN:{
                ChatRoomActivity.getInstance().exitRoom();
                break;
            }

            case ROOM_INVITE_FRIEND_LIST:{
                ChatRoomActivity.OpenInviteFriendDialog(r);
                break;
            }

            case ROOM_OPEN_INVITE_ACCPET:{
                LobbyActivity.InviteRoomAccpetDialog(r);
                break;
            }

            case FRIEND_NOTICE_MSG:{
                Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());
                DevTools.showToastShortMsg(activity,r.readLengthAsciiString());
                break;
            }

            case FRIEND_SEND_USER_LIST:{
                FriendActivity.getInstance().readFriendList(r);
                break;
            }

            case FRIEND_SEND_REQUEST_LIST:{
                FriendActivity.getInstance().readRequestList(r);
                break;
            }

            case ROOM_START_GAME_ACTIVITY:{
                ChatRoomActivity.getInstance().handleJoinGameActivity(r);
                break;
            }

            case GAME_BOOM_SPIN:{
                BoomSpinActivity.getInstance().handleBoomSpinReceive(r);
                break;
            }


        }


    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public int getChannel() {
        return channel;
    }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
