import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HTTPClient {
    public static void main(String args[]) throws IOException {
        Socket socket = null;
        PrintStream writer = null;
        BufferedReader reader = null;

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

        // 接收响应状态
        InputStream socketIS = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(socketIS));
        String firstLineOfResponse = reader.readLine();  // HTTP/1.1 200 OK
        String secondLineOfResponse = reader.readLine(); // Content-Type:text/html
        String thirdLineOfResponse = reader.readLine();  // Content-Length:
        String fourthLineOfResponse = reader.readLine(); // blank line

        if (firstLineOfResponse.endsWith("OK")) {
            // 读取响应数据，保存文件
            System.out.println("Transmission starts...");
            byte b[] = new byte[1024];
            String saveLocation = "C:\\Users\\舒意恒\\Documents\\GitHub\\HTTP-application\\saveLocation"; // 保存的位置
            FileOutputStream fileOS = new FileOutputStream(saveLocation + "/" + filename);
            int len = socketIS.read(b);
            while (len != -1) {
                fileOS.write(b, 0, len);
                len = socketIS.read(b);
            }
            socketIS.close();
            fileOS.close();
        } else {
            // 响应失败（状态码404）：将响应信息打印在控制台上
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            System.out.println(result);
        }
    }
}
