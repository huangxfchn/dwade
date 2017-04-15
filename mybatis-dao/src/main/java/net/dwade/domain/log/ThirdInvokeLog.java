package net.dwade.domain.log;

import java.io.Serializable;
import java.util.Date;

/**
 * 调用第三方接口日志表模型
 * @author huangxf
 * @date 2017-04-15
 */
public class ThirdInvokeLog implements Serializable {
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
     *  接口端系统名,如：支付宝、银联等  
     */
    private String resourceSysName;

    /**
     *  接口名  
     */
    private String resourceName;

    /**
     *  接口类型  1 rest/http  2 webservice  
     */
    private String resourceType;

    /**
     *  调用入口  
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
     *  接口发起端所在的IP  
     */
    private String appIp;

    /**
     *  接口调用耗时  
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
     * @return 接口端系统名,如：支付宝、银联等  
     */
    public String getResourceSysName() {
        return resourceSysName;
    }

    /**
     * 接口端系统名,如：支付宝、银联等 
     */
    public void setResourceSysName(String resourceSysName) {
        this.resourceSysName = resourceSysName == null ? null : resourceSysName.trim();
    }

    /**
     * @return 接口名  
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * 接口名 
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName == null ? null : resourceName.trim();
    }

    /**
     * @return 接口类型  1 rest/http  2 webservice  
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * 接口类型 1 rest/http 2 webservice 
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType == null ? null : resourceType.trim();
    }

    /**
     * @return 调用入口  
     */
    public String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * 调用入口 
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
     * @return 接口发起端所在的IP  
     */
    public String getAppIp() {
        return appIp;
    }

    /**
     * 接口发起端所在的IP 
     */
    public void setAppIp(String appIp) {
        this.appIp = appIp == null ? null : appIp.trim();
    }

    /**
     * @return 接口调用耗时  
     */
    public String getResponseTime() {
        return responseTime;
    }

    /**
     * 接口调用耗时 
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
        sb.append(", resourceSysName=").append(resourceSysName);
        sb.append(", resourceName=").append(resourceName);
        sb.append(", resourceType=").append(resourceType);
        sb.append(", resourceUrl=").append(resourceUrl);
        sb.append(", action=").append(action);
        sb.append(", returnCode=").append(returnCode);
        sb.append(", returnMsg=").append(returnMsg);
        sb.append(", inParam=").append(inParam);
        sb.append(", outParam=").append(outParam);
        sb.append(", appIp=").append(appIp);
        sb.append(", responseTime=").append(responseTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}