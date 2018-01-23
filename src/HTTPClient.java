import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPClient {
    private static String saveLocation = "C:\\Users\\舒意恒\\Documents\\GitHub\\HTTP-application\\saveLocation"; // 保存的位置

    public static void main(String args[]) throws IOException {
        while (true) {
            System.out.println("Enter the filename:");
            Scanner sc = new Scanner(System.in);
            String fileName = sc.nextLine();
            getFile(fileName);

            if (fileName.endsWith(".html") || fileName.endsWith("htm"))// 请求的是HTML文档
            {
                File HTMLdoc = new File(saveLocation + "/" + fileName);
                Document doc = Jsoup.parse(HTMLdoc, "UTF-8");
                Elements imgs = doc.select("img");
                for (Element img : imgs) {
                    String url = img.attr("src"); // 获得相对路径
                    getFile(url);   // 请求HTML文档中的jpg
                }
                Elements css = doc.select("link[href$=.css]");
                for (Element c : css) {
                    String url = c.attr("href");
                    getFile(url);
                }
                Elements scripts = doc.select("script[src$=.js]");
                for (Element js : scripts) {
                    String url = js.attr("src");
                    getFile(url);
                }
            }
        }
    }

    private static boolean readLines(InputStream is) throws IOException {
        boolean endTag = false;
        ArrayList<Character> charList = new ArrayList<>();
        int b = 0;
        while (true) {
            b = is.read();
            charList.add((char) b);
            if (b == '\n')
                break;
        }
        StringBuilder line = new StringBuilder();
        for (char ch : charList)
            line.append(String.valueOf(ch));
        if (line.toString().equals("\r\n"))
            endTag = true;
        return !endTag;
    }

    private static void getFile(String fileName) throws IOException {
        Socket socket;
        PrintStream writer;
        InputStream socketIS;

        // 连接服务器
        socket = new Socket("localhost", 8888);
        // 发送请求头
        writer = new PrintStream(socket.getOutputStream());
        writer.println("GET /" + fileName + " HTTP/1.1");
        writer.println("Host:localhost");
        writer.println("connetion:keep-alive");
        writer.println();
        writer.flush();
        // 接收响应状态
        socketIS = socket.getInputStream();
        DataInputStream dataIS = new DataInputStream(socketIS);
        // 处理响应报文第一行
        int b = 0;
        ArrayList<Character> charList = new ArrayList<>();
        while (true) {
            b = dataIS.read();
            charList.add((char) b);
            if (b == '\n')
                break;
        }
        StringBuilder firstLineOfResponse = new StringBuilder();
        for (char ch : charList) {
            firstLineOfResponse.append(String.valueOf(ch));
        }
        System.out.println(firstLineOfResponse);

        while (readLines(dataIS)) ;

        if (firstLineOfResponse.toString().contains("200 OK")) {
            // 读取响应数据，保存文件
            System.out.println("Transmission starts...");

            FileOutputStream fileOS = new FileOutputStream(saveLocation + "/" + fileName);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = dataIS.read(bytes)) != -1) {
                fileOS.write(bytes, 0, len);
                fileOS.flush();
            }
            fileOS.close();

            System.out.println("Transmission complete.");
        }
        socket.close(); // 关闭连接
    }
}
