package com.hopestarting.checkdome.cardReader;

import com.sun.jna.win32.StdCallLibrary;

public interface CardReaderInterface extends StdCallLibrary {

    // CardReaderInterface INSTANCE = (CardReaderInterface)
    // Native.loadLibrary("dcrf32", CardReaderInterface.class);// 加载动态库文件

    /**
     * @Description: 功 能：初始化通讯口
     * 
     *               参 数：port：取值为0～19时，表示串口1～20；为100时，表示USB口通讯，此时波特率无效。
     * 
     *               baud：为通讯波特率9600～115200
     * 
     *               返 回：成功则返回串口标识符>0，失败返回负值，见错误代码表
     * 
     *               例：int icdev;
     * 
     *               icdev=dc_init(0,9600);//初始化串口1，波特率9600
     * 
     * @author Huozb
     * @date 2018年5月16日 下午4:19:17
     */
    int dc_init(int port, int baud);// 动态库中调用的方法

    /**
     * @Description: 功 能：寻卡请求
     * 
     *               参 数：icdev：通讯设备标识符
     * 
     *               _Mode：寻卡模式mode_card
     * 
     *               Tagtype：卡类型值，详情见附录TagType特征值
     * 
     *               返 回：成功则返回 0
     * 
     *               例：int st;
     * 
     *               unsigned int *tagtype;
     * 
     *               st=dc_request(icdev,0,tagtype);
     * 
     * @author Huozb
     * @date 2018年5月16日 下午4:20:18
     */
    int dc_request(int icdev, int _Mode, int[] TagType);

    /**
     * 说明：设置读写器将要对哪一种卡操作，读写器上电缺省的时候是对TYPEA操作。
     * 
     * 
     * 参数说明：HANDLE icdev dc_init返回的设备描述符；
     * cardtype：当为'A'的时候表示设置读写器，'B'表示对TYPE_B操作
     * 
     * '1':15693?
     * 
     * 返回：成功则返回 0；
     * 
     * 
     * @param icdev
     * @param cardtype
     * @return
     */
    int dc_config_card(int icdev, int cardtype);

    /**
     * 15693专用函数
     * 
     * 功 能：寻卡，能返回在工作区域内卡的序列号（UID）及DSFID
     * 
     * 参 数：icdev：通讯设备标识符
     * 
     * flags： Request flags，符合ISO5693的标准
     * 
     * 根据flags的不同，可以有两种寻卡方式，0x36表示寻单张卡，0x16可以寻多张卡
     * 
     * AFI： 应用标识，符合ISO5693的标准
     * 
     * masklen: 掩码长度
     * 
     * rlen: 返回长度，根据寻卡的模式不同
     * 
     * rbuffer：如果masklen长度不为0，这里要放MASK VALUE
     * 
     * 执行成功后放返回数据
     * 
     * 如果是寻单张卡，就返回9字节（DSFID＋UID
     * 
     * 其中DSFID= rbuffer[0] UID= rbuffer[1]---- rbuffer[8]
     * 
     * 如果是寻多张卡，就返回［9*寻卡张数］个字节，例如寻到两张卡
     * 
     * 数据长度为18个字节，内容为：
     * 
     * DSFID1= rbuffer[0] UID1= rbuffer[1]---- rbuffer[8]
     * 
     * DSFID2= rbuffer[9] UID1= rbuffer[10]---- rbuffer[17]
     * 
     * 返 回：成功 0
     * 
     * 例：int st;
     * 
     * st= dc_inventory (icdev,0x36,AFI,0,&rlen,rbuffer);//寻单卡
     * 
     * st= dc_inventory (icdev,0x16,AFI,0,&rlen,rbuffer);//寻多卡
     * 
     */
    int dc_inventory(int icdev, int flags, int AFI, int masklen, byte[] rlen, byte[] rbuffer);

    /**
     * 15693专用函数
     * 
     * 功能：读块数据
     * 
     * 参数：icdev：通讯设备标识符
     * 
     * flags： Request flags
     * 
     * startblock： 起始块地址 范围 0－27或0－63
     * 
     * blocknum：块数 一次性读块数 1－10
     * 
     * UID： 卡号Unique identifier IN
     * 
     * rlen： 返回数据长度
     * 
     * rbuffer：返回块内数据
     * 
     * 返回：成功 0
     * 
     * 例：st=dc_readblock(icdev,0x22,7,3,&UID[1],&rlen,rbuffer);
     * 
     * 
     */
    int dc_readblock(int icdev, int flags, int startblock, int blocknum, byte[] UID, byte[] rlen, byte[] rbuffer);

    /**
     * @Description:功 能：读取卡中数据
     * 
     *                对于M1卡，一次读一个块的数据，为16个字节；
     * 
     *                对于ML卡，一次读出相同属性的两页（0和1，2和3，...），为8个字节
     * 
     *                参 数：icdev：通讯设备标识符
     * 
     *                _Adr：M1卡——块地址（0～63）,MS70(0-255)；
     * 
     *                ML卡——页地址（0～11）
     * 
     *                _Data：读出数据
     * 
     *                返 回：成功则返回 0
     * 
     *                例：int st;
     * 
     *                unsigned char data[16];
     * 
     *                st=dc_read(icdev,4,data); //读M1卡块4的数据
     * 
     *                相关HEX函数：
     * 
     *                __int16 __stdcall dc_read_hex(HANDLE icdev,unsigned char
     *                _Adr,char *_Data)
     * 
     * @author Huozb
     * @date 2018年5月16日 下午4:21:25
     */
    int dc_read(int icdev, int _Adr, byte[] _Data);

    /**
     * @Description: 功 能：关闭端口
     * 
     *               参 数：icdev：通讯设备标识符
     * 
     *               返 回：成功返回0
     * 
     *               例：dc_exit(icdev);
     * 
     *               注：在WIN32环境下icdev为端口的设备句柄，必须释放后才可以再次连接。
     * 
     * @author Huozb
     * @date 2018年5月16日 下午4:22:32
     */
    int dc_exit(int icdev);

    /**
     * 
     * 说明：射频复位函数
     * 
     * 调用： icdev ----通讯设备端口标识符
     * 
     * _Msec ----复位时间,单位为毫秒(此值为0时是关闭射频,为1,2...为复位1毫秒,2毫秒...)
     * 
     * 返回： <0 错误。其绝对值为错误号
     * 
     * =0 成功。
     * 
     * 
     * 
     */
    int dc_reset(int icdev, int _Msec);

    /**
     * @Description: 功 能：蜂鸣
     * 
     *               参 数：icdev：通讯设备标识符
     * 
     *               unsigned int _Msec：蜂鸣时间，单位是10毫秒
     * 
     *               返 回：成功则返回 0
     * 
     *               例：int st;
     * 
     *               st=dc_beep(icdev,10); //鸣叫100毫秒
     *
     * 
     * @author Huozb
     * @date 2018年5月16日 下午8:29:22
     */
    int dc_beep(int icdev, int _Msec);
}
