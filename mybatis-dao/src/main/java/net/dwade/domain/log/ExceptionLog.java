package net.dwade.domain.log;

import java.io.Serializable;
import java.util.Date;

/**
 * 异常日志表模型
 * @author huangxf
 * @date 2017-04-15
 */
public class ExceptionLog implements Serializable {
    /**
     *  ID  
     */
    private String id;

    /**
     *  日志追踪ID  
     */
    private String traceId;

    /**
     *  时间  
     */
    private Date time;

    /**
     *  应用名  
     */
    private String appName;

    /**
     *  异常名称  
     */
    private String exceptionName;

    /**
     *  异常信息  
     */
    private String exceptionInfo;

    /**
     *  应用所在IP  
     */
    private String appIp;

    private static final long serialVersionUID = 1L;

    /**
     * @return ID  
     */
    public String getId() {
        return id;
    }

    /**
     * ID 
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return 日志追踪ID  
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 日志追踪ID 
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId == null ? null : traceId.trim();
    }

    /**
     * @return 时间  
     */
    public Date getTime() {
        return time;
    }

    /**
     * 时间 
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return 应用名  
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 应用名 
     */
    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    /**
     * @return 异常名称  
     */
    public String getExceptionName() {
        return exceptionName;
    }

    /**
     * 异常名称 
     */
    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName == null ? null : exceptionName.trim();
    }

    /**
     * @return 异常信息  
     */
    public String getExceptionInfo() {
        return exceptionInfo;
    }

    /**
     * 异常信息 
     */
    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo == null ? null : exceptionInfo.trim();
    }

    /**
     * @return 应用所在IP  
     */
    public String getAppIp() {
        return appIp;
    }

    /**
     * 应用所在IP 
     */
    public void setAppIp(String appIp) {
        this.appIp = appIp == null ? null : appIp.trim();
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
        sb.append(", appName=").append(appName);
        sb.append(", exceptionName=").append(exceptionName);
        sb.append(", exceptionInfo=").append(exceptionInfo);
        sb.append(", appIp=").append(appIp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}