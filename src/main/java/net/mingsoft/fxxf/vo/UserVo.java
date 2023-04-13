package net.mingsoft.fxxf.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: yrg
 * @Date: 2020-01-13 15:03
 * @Description: 用户信息
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserVo {

    private Integer id;

    /**
     * 用户名
     */
    private String account;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 所在省
     */
    private String province;

    /**
     * 所在市
     */
    private String city;

    /**
     * 邮政编码
     */
    private String zipcode;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 用户类型
     */
    private Integer usertype;

}
