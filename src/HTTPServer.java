/**
 * HTTP 服务器，支持多线程
 * 支持使用浏览器访问资源
 *
 * @author 舒意恒
 */

import java.net.ServerSocket;
import java.net.Socket;


public class HTTPServer {
    public static void main(String[] args) {
        final String F_DIR = "";
        final int PORT = 8888;
        System.out.println("Server port: " + PORT);
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                // 接受客户端请求
                Socket client = ss.accept();
                // 创建服务线程
                new ThreadOnServer(client, F_DIR).start();
                System.out.println(client.toString() + "  " + F_DIR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

