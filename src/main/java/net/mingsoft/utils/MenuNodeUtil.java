package net.mingsoft.utils;


import net.mingsoft.fxxf.bean.vo.MenuNode;

import java.util.ArrayList;
import java.util.List;

public class MenuNodeUtil {

    /**
     * @param treeDataList
     * @return 返回类型：List<JsonTreeData>
     * @Title: getfatherNode
     * @Description 方法描述: 父节点
     */
    public final static List<MenuNode> getfatherNode(List<MenuNode> treeDataList) {
        List<MenuNode> newTreeDataList = new ArrayList<MenuNode>();
        for (MenuNode menu : treeDataList) {
            if (menu.getPid() == 0) {
                // 获取父节点下的子节点
                menu.setChildren(getChildrenNode(menu.getId(), treeDataList));
                newTreeDataList.add(menu);
            }
        }
        return newTreeDataList;
    }

    /**
     * @param pid
     * @param treeDataList
     * @return 返回类型：List<JsonTreeData>
     * @Title: getChildrenNode
     * @Description 方法描述: 子节点
     */
    private final static List<MenuNode> getChildrenNode(int pid, List<MenuNode> treeDataList) {
        List<MenuNode> newTreeDataList = new ArrayList<MenuNode>();
        for (MenuNode menu : treeDataList) {
            if (menu.getPid() == 0)
                continue;
            // 这是一个子节点
            if (menu.getPid() == pid) {
                // 递归获取子节点下的子节点
                menu.setChildren(getChildrenNode(menu.getId(), treeDataList));
                newTreeDataList.add(menu);
            }
        }
        return newTreeDataList;
    }

}