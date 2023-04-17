package net.mingsoft.fxxf.bean.enums;

/**
 * 审核状态 状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:市级审核通过; 7:审核不通过 )
 *
 * @author: huangjunjian
 */
public enum AuditStatusEnum {
    /**
     * 摘牌
     */
    DELIST(0, "摘牌"),

    /**
     * 在期
     */
    VALID(1, "在期"),

    /**
     * 过期
     */
    EXPIRE(2, "过期"),

    /**
     * 待审核
     */
    AUDIT_WAITTING(4, "待审核"),

    /**
     * 放心消费承诺单位
     */
    COUNTY_APPROVE(5, "县级审核通过"),

    /**
     * 市级审核通过
     */
    CITY_APPROVE(6, "市级审核通过"),

    /**
     * 审核不通过
     */
    AUDIT_NO_PASS(7, "审核不通过");

    private int code;

    private String name;

    AuditStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AuditStatusEnum matchByCode(Integer code) {
        AuditStatusEnum result = null;

        for (AuditStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                result = e;
                break;
            }
        }

        return result;
    }

    public static AuditStatusEnum matchByFieldName(String fieldName) {
        AuditStatusEnum result = null;

        for (AuditStatusEnum e : values()) {
            if (e.getName().equals(fieldName)) {
                result = e;
                break;
            }
        }

        return result;
    }
}
