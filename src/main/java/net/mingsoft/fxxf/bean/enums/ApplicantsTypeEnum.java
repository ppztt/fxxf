package net.mingsoft.fxxf.bean.enums;

/**
 * 单位类型 1、放心消费承诺单位 2、无理由退货单位
 *
 * @author: huangjunjian
 */
public enum ApplicantsTypeEnum {
    /**
     * 放心消费承诺单位
     */
    UNIT(1, "放心消费承诺单位"),

    /**
     * 无理由退货单位
     */
    STORE(2, "无理由退货单位");

    private int code;

    private String name;

    ApplicantsTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ApplicantsTypeEnum matchByCode(Integer code) {
        ApplicantsTypeEnum result = null;

        for (ApplicantsTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                result = e;
                break;
            }
        }

        return result;
    }

    public static ApplicantsTypeEnum matchByFieldName(String fieldName) {
        ApplicantsTypeEnum result = null;

        for (ApplicantsTypeEnum e : values()) {
            if (e.getName().equals(fieldName)) {
                result = e;
                break;
            }
        }

        return result;
    }
}
