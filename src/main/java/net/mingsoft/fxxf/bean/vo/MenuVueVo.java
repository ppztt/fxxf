package net.mingsoft.fxxf.bean.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: yrg
 * @Date: 2020-01-14 11:17
 * @Description: 菜单列表
 **/
@Data
@NoArgsConstructor
public class MenuVueVo {

    private Integer id;
    private Integer pid;
    private String title;
    private boolean expand = true;
    private boolean checked;
    private List<MenuVueVo> children;

    public MenuVueVo(Integer id, Integer pid, String title, boolean expand, boolean checked, List<MenuVueVo> children) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.expand = expand;
        this.checked = checked;
        this.children = children;
    }
}
