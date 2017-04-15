package net.dwade.domain.dict;

import java.io.Serializable;

/**
 * 操作日志字段映射表模型
 * @author huangxf
 * @date 2017-04-15
 */
public class OptLogFieldMapping implements Serializable {
    /**
     *  ID  
     */
    private String id;

    /**
     *  功能ID  
     */
    private String funcId;

    /**
     *  字段名称  
     */
    private String fieldName;

    /**
     *  入出参标识：0 入参 1 出参  
     */
    private String paramFlag;

    /**
     *  字段对应XPATH  
     */
    private String paramXpath;

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
     * @return 功能ID  
     */
    public String getFuncId() {
        return funcId;
    }

    /**
     * 功能ID 
     */
    public void setFuncId(String funcId) {
        this.funcId = funcId == null ? null : funcId.trim();
    }

    /**
     * @return 字段名称  
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 字段名称 
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName == null ? null : fieldName.trim();
    }

    /**
     * @return 入出参标识：0 入参 1 出参  
     */
    public String getParamFlag() {
        return paramFlag;
    }

    /**
     * 入出参标识：0 入参 1 出参 
     */
    public void setParamFlag(String paramFlag) {
        this.paramFlag = paramFlag == null ? null : paramFlag.trim();
    }

    /**
     * @return 字段对应XPATH  
     */
    public String getParamXpath() {
        return paramXpath;
    }

    /**
     * 字段对应XPATH 
     */
    public void setParamXpath(String paramXpath) {
        this.paramXpath = paramXpath == null ? null : paramXpath.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", funcId=").append(funcId);
        sb.append(", fieldName=").append(fieldName);
        sb.append(", paramFlag=").append(paramFlag);
        sb.append(", paramXpath=").append(paramXpath);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}