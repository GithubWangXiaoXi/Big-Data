package wangxiaoxi.socketTest;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.Socket;

public class ServerServiceThread implements Runnable{

    Socket socket = null;
    InputStream in =null;
    OutputStream out = null;

    public ServerServiceThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            //获得客户端的输入输出流
            in = socket.getInputStream();
            out = socket.getOutputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String data = bufferedReader.readLine();
            System.out.println(data);

            String[] tokens = null;
            String classname = null;
            String methodName = null;
            String param = null;
            Object result = new Object();

            if(data.contains(",")){
                tokens = data.split(",");
            }

            if(tokens != null){
                classname = tokens[0];
                methodName = tokens[1];
                param = tokens[2];
            }

            System.out.println("class:"+classname);
            System.out.println("method:"+methodName);
            System.out.println("param:"+param);

            if (classname!=null && methodName != null && param!=null){
                Class clazz = Class.forName(classname);
                Method method = clazz.getMethod(methodName,String.class);
                result = method.invoke(clazz.newInstance(),param);
            }

            System.out.println(result);

            PrintWriter printWriter = new PrintWriter(out);
            printWriter.println(result);
            printWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
