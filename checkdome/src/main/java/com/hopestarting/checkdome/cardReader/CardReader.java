package com.hopestarting.checkdome.cardReader;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.hopestarting.checkdome.common.domain.MessageVO;
import com.sun.jna.Native;

public class CardReader {
    private static CardReader cardReader;
    private static CardReaderInterface instance;// 加载动态库文件
    private static int icdev;// 设备句柄号
    private final int port = 100;// 使用usb端口
    private final int baud = 115200;// 波特率 115200

    private int _Mode = 0;// 通用函数 0——表示IDLE模式，一次只对一张卡操作；
    private int[] _TagType = { 6, 8 };// 通用函数 卡类型值 ULTRA LIGHT {6,8}

    private int m_StaAddr = 8;// 数据起始地址
    private int m_Blockno = 3;// 读取数据块数量
    private int lenght = 10;// 数据长度

    private CardReader(File file) {
        if (instance == null) {
            instance = (CardReaderInterface) Native.loadLibrary(file.getAbsolutePath(), CardReaderInterface.class);// 加载动态库文件
        }
        init();
    }

    public static CardReader getCardReader(File file) throws Exception {
        if (cardReader != null && icdev > 0) {
            return cardReader;
        }
        cardReader = new CardReader(file);
        return cardReader;
    }

    private void init() {
        icdev = instance.dc_init(port, baud);
        if (icdev > 0) {
            if(instance.dc_config_card(icdev, '1') == 0){
                instance.dc_beep(icdev, 200);
            }
        }
    }

    /**
     * @Description:确认初始化
     * @return
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:39:28
     */
    public MessageVO isInit() {
        MessageVO msg = new MessageVO();
        if (icdev > 0) {
            msg.setSuccess(true);
        } else {
            msg.setSuccess(false);
            msg.setMsg(icdev + "");
        }
        return msg;
    }

    /**
     * @Description: 是否有卡
     * @return
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:39:44
     */
    public MessageVO hasCard() {
        MessageVO msg = new MessageVO();
        int resule = instance.dc_request(icdev, _Mode, _TagType);
        instance.dc_reset(icdev, 10);
        if (resule == 0) {
            msg.setSuccess(true);
            instance.dc_beep(icdev, 30);
        } else {
            msg.setSuccess(false);
            msg.setMsg(resule + "");
        }
        return msg;
    }

    /**
     * @Description:读取数据
     * @return
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:40:02
     */
    public MessageVO read() {
        MessageVO msg = new MessageVO();
        byte[] _Data = new byte[lenght];
        int resule = instance.dc_read(icdev, m_StaAddr, _Data);
        if (resule == 0) {
            instance.dc_beep(icdev, 20);
            instance.dc_beep(icdev, 20);
            msg.setSuccess(true);
            try {
                msg.setData(new String(_Data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            msg.setSuccess(false);
            msg.setMsg(resule + "");
        }
        return msg;
    }

    /**
     * @Description: 是否有卡
     * @return
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:39:44
     */
    public MessageVO hasCardFor15693() {
        MessageVO msg = new MessageVO();
        int flags = 0x36;
        int AFI = 0;
        int masklen = 0;
        byte[] UID = new byte[128];
        byte[] rlen = new byte[1];
        int resule = instance.dc_inventory(icdev, flags, AFI, masklen, rlen, UID);
        if (resule == 0) {
            msg.setSuccess(true);
            byte[] newUID = new byte[rlen[0]];
            System.arraycopy(UID, 1, newUID, 0, rlen[0] - 1);
            msg.setData(newUID);
            instance.dc_beep(icdev, 30);
        } else {
            msg.setSuccess(false);
            msg.setMsg(resule + "");
        }
        return msg;
    }

    /**
     * @Description:读取数据
     * @return
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:40:02
     */
    public MessageVO readFor15693(byte[] UID) {
        MessageVO msg = new MessageVO();
        int flags = 0x22;
        byte[] rlen = new byte[1];
        byte[] rbuffer = new byte[lenght];
        int resule = instance.dc_readblock(icdev, flags, m_StaAddr, m_Blockno, UID, rlen, rbuffer);
        instance.dc_reset(icdev, 10);
        if (resule == 0) {
            instance.dc_beep(icdev, 20);
            instance.dc_beep(icdev, 20);
            msg.setSuccess(true);
            try {
                msg.setData(new String(rbuffer, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            msg.setSuccess(false);
            msg.setMsg(resule + "");
        }
        return msg;
    }

    /**
     * @Description:蜂鸣
     * @param _Msec
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:40:15
     */
    public void beep(int _Msec) {
        instance.dc_beep(icdev, _Msec);
    }

    /**
     * @Description:断开链接
     * @return
     *
     * @author Huozb
     * @date 2018年5月16日 下午8:40:43
     */
    public MessageVO exit() {
        MessageVO msg = new MessageVO();
        instance.dc_beep(icdev, 20);
        instance.dc_beep(icdev, 20);
        instance.dc_beep(icdev, 20);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        instance.dc_exit(icdev);
        cardReader = null;
        return msg;
    }

}
