package net.mingsoft.fxxf.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuNode {

	private int id;
	private int pid;
	private String text;
	private String url;
	private String iconCls;
	private String type;
	private List<MenuNode> children;

}