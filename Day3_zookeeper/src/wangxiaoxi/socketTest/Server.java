package wangxiaoxi.socketTest;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static String host = "localhost";
    private static int port = 8899;

    public static void main(String[] args) throws Exception{
        //创建socket连接，并绑定ip（主机名）和端口号
        ServerSocket serversocket = new ServerSocket();
        serversocket.bind(new InetSocketAddress(host,port));

        //server需要等待client来申请连接，此时server为阻塞状态
        while(true){
            //socket是客户端套接字，而serverSocket是服务器端套接字
            Socket socket = serversocket.accept();
            //通过多线程来处理多客户访问服务器的业务逻辑
            new Thread(new ServerServiceThread(socket)).start();
        }
    }
}
