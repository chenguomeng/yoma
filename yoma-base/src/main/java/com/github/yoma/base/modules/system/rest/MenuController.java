package com.github.yoma.base.modules.system.rest;

import com.github.yoma.common.exception.BadRequestException;
import com.github.yoma.base.modules.system.domain.Menu;
import com.github.yoma.base.modules.system.service.MenuService;
import com.github.yoma.base.modules.system.service.RoleService;
import com.github.yoma.base.modules.system.service.UserService;
import com.github.yoma.base.modules.system.service.dto.MenuDTO;
import com.github.yoma.base.modules.system.service.dto.MenuQueryCriteria;
import com.github.yoma.base.modules.system.service.dto.UserDTO;
import com.github.yoma.common.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
@Api(tags = "系统：菜单管理")
@RestController
@RequestMapping("/api/menus")
@SuppressWarnings("unchecked")
public class MenuController {

    private final MenuService menuService;

    private final UserService userService;

    private final RoleService roleService;

    private static final String ENTITY_NAME = "menu";

    public MenuController(MenuService menuService, UserService userService, RoleService roleService) {
        this.menuService = menuService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @ApiOperation("导出菜单数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('menu:list')")
    public void download(HttpServletResponse response, MenuQueryCriteria criteria) throws IOException {
        menuService.download(menuService.queryAll(criteria), response);
    }

    @ApiOperation("获取前端所需菜单")
    @GetMapping(value = "/build")
    public ResponseEntity buildMenus() {
        UserDTO user = userService.findByName(SecurityUtils.getUsername());
        List<MenuDTO> menuDTOList = menuService.findByRoles(roleService.findByUsers_Id(user.getId()));
        List<MenuDTO> menuDTOS = (List<MenuDTO>) menuService.buildTree(menuDTOList).get("content");
        return new ResponseEntity<>(menuService.buildMenus(menuDTOS), HttpStatus.OK);
    }

    @ApiOperation("返回全部的菜单")
    @GetMapping(value = "/tree")
    @PreAuthorize("@el.check('menu:list','roles:list')")
    public ResponseEntity getMenuTree() {
        return new ResponseEntity<>(menuService.getMenuTree(menuService.findByParentId(0L)), HttpStatus.OK);
    }

    @ApiOperation("查询菜单")
    @GetMapping
    @PreAuthorize("@el.check('menu:list')")
    public ResponseEntity getMenus(MenuQueryCriteria criteria) {
        List<MenuDTO> menuDTOList = menuService.queryAll(criteria);
        return new ResponseEntity<>(menuService.buildTree(menuDTOList), HttpStatus.OK);
    }

    @ApiOperation("新增菜单")
    @PostMapping
    @PreAuthorize("@el.check('menu:add')")
    public ResponseEntity create(@Validated @RequestBody Menu resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        return new ResponseEntity<>(menuService.create(resources), HttpStatus.CREATED);
    }

    @ApiOperation("修改菜单")
    @PutMapping
    @PreAuthorize("@el.check('menu:edit')")
    public ResponseEntity update(@Validated(Menu.Update.class) @RequestBody Menu resources) {
        menuService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ApiOperation("删除菜单")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@el.check('menu:del')")
    public ResponseEntity delete(@PathVariable Long id) {
        List<Menu> menuList = menuService.findByParentId(id);
        Set<Menu> menuSet = new HashSet<>();
        menuSet.add(menuService.findOne(id));
        menuSet = menuService.getDeleteMenus(menuList, menuSet);
        menuService.delete(menuSet);
        return new ResponseEntity(HttpStatus.OK);
    }
}
