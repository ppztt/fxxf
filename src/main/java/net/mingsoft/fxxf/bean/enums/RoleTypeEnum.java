package net.mingsoft.fxxf.bean.enums;

/**
 * 系统角色
 *
 * @author: huangjunjian
 */
public enum RoleTypeEnum {
    /**
     * 省工作人员
     */
    PROVINCE(1, "省工作人员"),

    /**
     * 地市工作人员
     */
    CITY(2, "地市工作人员"),

    /**
     * 区县工作人员
     */
    COUNTY(3, "区县工作人员"),

    /**
     * 行业协会
     */
    BUSINESS(4, "行业协会");

    private int code;

    private String name;

    RoleTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RoleTypeEnum matchByCode(Integer code) {
        RoleTypeEnum result = null;

        for (RoleTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                result = e;
                break;
            }
        }

        return result;
    }

    public static RoleTypeEnum matchByFieldName(String fieldName) {
        RoleTypeEnum result = null;

        for (RoleTypeEnum e : values()) {
            if (e.getName().equals(fieldName)) {
                result = e;
                break;
            }
        }

        return result;
    }
}
