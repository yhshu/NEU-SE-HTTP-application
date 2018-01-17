import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HTTPClient {
    public static void main(String args[]) {
        Socket socket = null;
        PrintStream writer = null;
        BufferedReader reader = null;

        try {
            Scanner sc = new Scanner(System.in);
            // 连接服务器
            socket = new Socket("localhost", 8888);
            // 发送请求头
            writer = new PrintStream(socket.getOutputStream());
            System.out.println("Enter the filename:");
            String filename = sc.next();
            writer.println("GET /" + filename + " HTTP/1.1");
            writer.println("Host:localhost");
            writer.println("connetion:keep-alive");
            writer.println();
            writer.flush();
            // 发送请求体（GET方式中，请求数据挂在URL后）
            // 接收响应状态

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.print("GET /index.htm \r\n" + "Host:");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
