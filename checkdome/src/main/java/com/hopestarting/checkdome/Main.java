package com.hopestarting.checkdome;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import com.hopestarting.checkdome.cardReader.CardReader;
import com.hopestarting.checkdome.common.domain.MessageVO;

public class Main {

    static CardReader cardReader = null;
    static Robot robot = null;

    public static void main(String[] args) throws AWTException, InterruptedException, IOException {
        Main main = new Main();
        String path = main.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, "UTF-8");
        int lastIndex = path.lastIndexOf("/");
        path = path.substring(0, lastIndex);
        File file = new File(path + "/dcrf32.dll");
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
            InputStream in = main.getClass().getResourceAsStream("/resources/dcrf32.dll");
            OutputStream out = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int read = 0;
            while ((read = in.read(b)) > 0) {
                out.write(b, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        }
        System.out.println(file.getAbsolutePath());
        // 创建机器人
        robot = new Robot();
        // 设置默认休眠时间
        robot.setAutoDelay(20);
        int initCount = 0;
        // 连接读卡器
        while (true) {
            try {
                cardReader = CardReader.getCardReader(file);
            } catch (Exception e1) {
                System.out.println("读卡器初始化失败!请检查设备连接!");
                break;
            }
            if (!cardReader.isInit().getSuccess()) {
                if (initCount > 2) {
                    System.out.println("读卡器初始化失败,错误码:" + cardReader.isInit().getMsg());
                    System.exit(0);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                initCount++;
            } else {
                break;
            }
        }
        System.out.println("读卡器初始化完成!");
        // 循坏读卡
        while (true) {
            try {
                // 循环监测是否有卡
                byte[] UID = null;
                while (true) {
                    MessageVO hasCard = cardReader.hasCardFor15693();
                    if (!hasCard.getSuccess()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } else {
                        UID = (byte[]) hasCard.getData();
                        break;
                    }
                }
                // 读卡 写出
                read(UID);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void read(byte[] UID) {
        String code = null;
        MessageVO readMsg = cardReader.readFor15693(UID);
        if (readMsg.getSuccess()) {
            code = readMsg.getData().toString();
        } else {
            return;
        }
        char[] codeArr = code.toCharArray();
        for (char c : codeArr) {
            int index = KeyMap.keyMap.get(c + "");
            robot.keyPress(index);
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}