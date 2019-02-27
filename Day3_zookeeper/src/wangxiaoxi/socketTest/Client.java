package wangxiaoxi.socketTest;

import java.io.*;
import java.net.Socket;

public class Client {

    private static String host = "localhost";
    private static int port = 8899;

    public static void main(String[] args) throws Exception{
        Socket socket = new Socket(host,port);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        PrintWriter printWriter = new PrintWriter(out);
        printWriter.println("wangxiaoxi.socketTest.ServiceImpl,"+"ServiceHandler,"+"this is your email");
        printWriter.flush();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String result = bufferedReader.readLine();
        System.out.println(result);

        in.close();
        out.close();
        socket.close();
    }
}
