package net.mingsoft.fxxf.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.fxxf.bean.entity.Type;
import net.mingsoft.fxxf.bean.vo.BaseResult;
import net.mingsoft.fxxf.bean.vo.TypeVo;
import net.mingsoft.fxxf.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-17
 */
@RestController
@RequestMapping("/type")
@Api(tags = { "类型"})
public class TypeController {

    @Autowired
    private TypeService typeService;


    @GetMapping(value = "/listGoodsAndServiceType")
    @ApiOperation(value = "获取type为5和6的数据", notes = "获取type为5和6的数据")
    public BaseResult<TypeVo> types() {

        try {
            List<Type> types = typeService.list(new QueryWrapper<Type>().in("type", Arrays.asList("5", "6")));

            ArrayList<String> list1 = new ArrayList<>();
            ArrayList<String> list2 = new ArrayList<>();
            if (!types.isEmpty()) {
                types.stream()
                        .filter(t -> t.getType() == 5)
                        .forEach(t -> list1.add(t.getName()));
                types.stream()
                        .filter(t -> t.getType() == 6)
                        .forEach(t -> list2.add(t.getName()));

            }
            return BaseResult.success(new TypeVo(list1, list2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResult.fail();

    }

}

