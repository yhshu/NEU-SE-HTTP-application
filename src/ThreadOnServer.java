import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Socket;

public class ThreadOnServer extends Thread {
    private Socket socketClient = null;
    private String path = "";

    public ThreadOnServer(Socket socketClient, String path) {
        this.socketClient = socketClient;
        this.path = path;
    }

    public void run() {
        InputStream is = null;
        OutputStream os = null;
        BufferedReader reader = null;
        PrintStream writer = null;
        String firstLineOfRequest = "";
        try {
            reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            writer = new PrintStream(socketClient.getOutputStream());
            firstLineOfRequest = reader.readLine();

            String uri = firstLineOfRequest.split(" ")[1];
            File file = new File("C:\\Users\\舒意恒\\Documents\\GitHub\\HTTP-application\\server_dir" + uri);
            if (file.isFile() && file.exists()) {
                // 如果文件存在
                writer.println("HTTP/1.1 200 OK"); // 返回应答消息，并结束应答
                if (uri.endsWith(".html")) {
                    writer.println("Content-Type:text/html");
                } else if (uri.endsWith(".jpg")) {
                    writer.println("Content-Type:image/jpeg");
                } else {
                    writer.println("Content-Type:application/octet-stream");
                }

                is = new FileInputStream("C:\\Users\\舒意恒\\Documents\\GitHub\\HTTP-application\\server_dir" + uri);

                // 发送响应头
                writer.println("Content-Lenth:" + is.available());
                writer.println();
                writer.flush();

                // 发送响应体
                os = new DataOutputStream(socketClient.getOutputStream());
                byte[] b = new byte[1024];
                int len = 0;
                len = is.read(b);
                while (len != -1) {
                    os.write(b, 0, len);
                    len = is.read(b);
                }
                os.flush();
                is.close();

                if (file.getName().endsWith("html") || file.getName().endsWith("htm"))// 应答的是HTML文档
                {
                    Document doc = Jsoup.parse(file, "UTF-8");
                    Elements jpgs = doc.select("img[src$=.jpg]");
                    for (Element jpg : jpgs) {
                        String url = jpg.attr("src"); // 获得相对路径
                        is = new FileInputStream("C:\\Users\\舒意恒\\Documents\\GitHub\\HTTP-application\\server_dir" + "/" + url);
                        b = new byte[1024];
                        len = is.read(b);
                        while (len != -1) {
                            os.write(b, 0, len);
                            len = is.read(b);
                        }
                        os.flush();
                        is.close();
                    }
                }
                writer.close();
            } else {
                // 如果文件不存在
                // 发送响应头
                writer.println("HTTP/1.1 404 Not Found");
                writer.println("Content-Type:text/plain");
                writer.println("Content-Length:7");
                writer.println();
                // 发送响应体
                writer.print("The requested resource does not exist.");
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
