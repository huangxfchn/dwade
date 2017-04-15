package net.dwade.domain.dict;

import java.io.Serializable;

/**
 * 字典表模型
 * @author huangxf
 * @date 2017-04-15
 */
public class Dictionary implements Serializable {
    /**
     *  ID  
     */
    private String id;

    /**
     *  类型 例如：orgStatus、payStatus、payItemStatus  
     */
    private String dicEntry;

    /**
     *  字典类型编码  
     */
    private String dicCode;

    /**
     *  字典值对应的意义  
     */
    private String dicValue;

    /**
     *  字典类型描述  
     */
    private String remark;

    /**
     *  U 在用 E 停用  
     */
    private String state;

    /**
     *  父ID  
     */
    private String praentId;

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
     * @return 类型 例如：orgStatus、payStatus、payItemStatus  
     */
    public String getDicEntry() {
        return dicEntry;
    }

    /**
     * 类型 例如：orgStatus、payStatus、payItemStatus 
     */
    public void setDicEntry(String dicEntry) {
        this.dicEntry = dicEntry == null ? null : dicEntry.trim();
    }

    /**
     * @return 字典类型编码  
     */
    public String getDicCode() {
        return dicCode;
    }

    /**
     * 字典类型编码 
     */
    public void setDicCode(String dicCode) {
        this.dicCode = dicCode == null ? null : dicCode.trim();
    }

    /**
     * @return 字典值对应的意义  
     */
    public String getDicValue() {
        return dicValue;
    }

    /**
     * 字典值对应的意义 
     */
    public void setDicValue(String dicValue) {
        this.dicValue = dicValue == null ? null : dicValue.trim();
    }

    /**
     * @return 字典类型描述  
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 字典类型描述 
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * @return U 在用 E 停用  
     */
    public String getState() {
        return state;
    }

    /**
     * U 在用 E 停用 
     */
    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    /**
     * @return 父ID  
     */
    public String getPraentId() {
        return praentId;
    }

    /**
     * 父ID 
     */
    public void setPraentId(String praentId) {
        this.praentId = praentId == null ? null : praentId.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dicEntry=").append(dicEntry);
        sb.append(", dicCode=").append(dicCode);
        sb.append(", dicValue=").append(dicValue);
        sb.append(", remark=").append(remark);
        sb.append(", state=").append(state);
        sb.append(", praentId=").append(praentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}