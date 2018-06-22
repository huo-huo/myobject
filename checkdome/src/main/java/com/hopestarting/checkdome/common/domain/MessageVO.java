package com.hopestarting.checkdome.common.domain;
/**
 * 
* @ClassName: MessageVO
* @Description: 返回提示信息的vo类
* @author Wangwei
* @date 2018年3月9日 下午2:21:11
*
 */
public class MessageVO {
    private Boolean success;
    private String msg;
    private Object data;
    
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public MessageVO(Boolean success, String msg) {
        super();
        this.success = success;
        this.msg = msg;
    }
    public MessageVO() {
        super();
    }
    @Override
    public String toString() {
        return "MessageVO [success=" + success + ", msg=" + msg + ", data=" + data + "]";
    }
    
}
