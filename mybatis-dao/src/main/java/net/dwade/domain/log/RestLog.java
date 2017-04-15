package net.dwade.domain.log;

import java.io.Serializable;
import java.util.Date;

/**
 * rest接口调用日志表模型
 * @author huangxf
 * @date 2017-04-15
 */
public class RestLog implements Serializable {
    /**
     *  日志ID  
     */
    private String id;

    /**
     *  TRACE_ID  
     */
    private String traceId;

    /**
     *  请求时间  
     */
    private Date time;

    /**
     *  商户标识  
     */
    private String appKey;

    /**
     *  资源url  
     */
    private String resourceUrl;

    /**
     *  操作 GET/POST/DELETE/PUT  
     */
    private String action;

    /**
     *  返回码  
     */
    private String returnCode;

    /**
     *  返回信息  
     */
    private String returnMsg;

    /**
     *  入参  
     */
    private String inParam;

    /**
     *  出参  
     */
    private String outParam;

    /**
     *  操作人  
     */
    private String operateId;

    /**
     *  用户IP  
     */
    private String clientIp;

    /**
     *  响应时长，单位ms  
     */
    private String responseTime;

    private static final long serialVersionUID = 1L;

    /**
     * @return 日志ID  
     */
    public String getId() {
        return id;
    }

    /**
     * 日志ID 
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return TRACE_ID  
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * TRACE_ID 
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId == null ? null : traceId.trim();
    }

    /**
     * @return 请求时间  
     */
    public Date getTime() {
        return time;
    }

    /**
     * 请求时间 
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return 商户标识  
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * 商户标识 
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? null : appKey.trim();
    }

    /**
     * @return 资源url  
     */
    public String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * 资源url 
     */
    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl == null ? null : resourceUrl.trim();
    }

    /**
     * @return 操作 GET/POST/DELETE/PUT  
     */
    public String getAction() {
        return action;
    }

    /**
     * 操作 GET/POST/DELETE/PUT 
     */
    public void setAction(String action) {
        this.action = action == null ? null : action.trim();
    }

    /**
     * @return 返回码  
     */
    public String getReturnCode() {
        return returnCode;
    }

    /**
     * 返回码 
     */
    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode == null ? null : returnCode.trim();
    }

    /**
     * @return 返回信息  
     */
    public String getReturnMsg() {
        return returnMsg;
    }

    /**
     * 返回信息 
     */
    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg == null ? null : returnMsg.trim();
    }

    /**
     * @return 入参  
     */
    public String getInParam() {
        return inParam;
    }

    /**
     * 入参 
     */
    public void setInParam(String inParam) {
        this.inParam = inParam == null ? null : inParam.trim();
    }

    /**
     * @return 出参  
     */
    public String getOutParam() {
        return outParam;
    }

    /**
     * 出参 
     */
    public void setOutParam(String outParam) {
        this.outParam = outParam == null ? null : outParam.trim();
    }

    /**
     * @return 操作人  
     */
    public String getOperateId() {
        return operateId;
    }

    /**
     * 操作人 
     */
    public void setOperateId(String operateId) {
        this.operateId = operateId == null ? null : operateId.trim();
    }

    /**
     * @return 用户IP  
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * 用户IP 
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp == null ? null : clientIp.trim();
    }

    /**
     * @return 响应时长，单位ms  
     */
    public String getResponseTime() {
        return responseTime;
    }

    /**
     * 响应时长，单位ms 
     */
    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime == null ? null : responseTime.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", traceId=").append(traceId);
        sb.append(", time=").append(time);
        sb.append(", appKey=").append(appKey);
        sb.append(", resourceUrl=").append(resourceUrl);
        sb.append(", action=").append(action);
        sb.append(", returnCode=").append(returnCode);
        sb.append(", returnMsg=").append(returnMsg);
        sb.append(", inParam=").append(inParam);
        sb.append(", outParam=").append(outParam);
        sb.append(", operateId=").append(operateId);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", responseTime=").append(responseTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}