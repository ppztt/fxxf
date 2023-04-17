package net.mingsoft.fxxf.bean.enums;

/**
 * 创建方式（企业提交； 区县导入； 地市导入）
 *
 * @author: huangjunjian
 */
public enum CreateTypeEnum {
    /**
     * 省级录入
     */
    PROVINCE_INPUT(1, "省级录入"),

    /**
     * 市级录入
     */
    CITY_INPUT(2, "市级录入"),

    /**
     * 区县录入
     */
    COUNTY_INPUT(3, "区县录入"),

    /**
     * 行业协会录入
     */
    BUSINESS_INPUT(4, "行业协会录入");

    private int code;

    private String name;

    CreateTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static CreateTypeEnum matchByCode(Integer code) {
        CreateTypeEnum result = null;

        for (CreateTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                result = e;
                break;
            }
        }

        return result;
    }

    public static CreateTypeEnum matchByFieldName(String fieldName) {
        CreateTypeEnum result = null;

        for (CreateTypeEnum e : values()) {
            if (e.getName().equals(fieldName)) {
                result = e;
                break;
            }
        }

        return result;
    }
}
