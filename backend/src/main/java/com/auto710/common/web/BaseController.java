package com.auto710.common.web;

import cn.hutool.core.util.StrUtil;
import com.auto710.common.api.PageResult;
import com.auto710.common.api.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.ResolvableType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通用 CRUD 控制器基类
 * <p>后台管理接口统一继承本类，默认提供分页查询、列表、详情、新增、修改、删除、批量删除。</p>
 *
 * @param <S> Service
 * @param <T> 实体
 */
public abstract class BaseController<S extends IService<T>, T> {

    private static final Set<String> RESERVED = new HashSet<>(List.of(
            "page", "size", "orderBy", "asc", "ids", "keyword", "beginTime", "endTime", "order"));

    protected final S service;
    protected final Class<T> entityClass;
    private final Map<String, Field> fieldCache = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected BaseController(S service) {
        this.service = service;
        ResolvableType type = ResolvableType.forClass(getClass()).as(BaseController.class);
        this.entityClass = (Class<T>) type.getGeneric(1).resolve();
        if (entityClass != null) {
            for (Class<?> c = entityClass; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field f : c.getDeclaredFields()) {
                    fieldCache.putIfAbsent(f.getName(), f);
                }
            }
        }
    }

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<T>> page(@RequestParam Map<String, Object> params) {
        long page = StrUtil.isBlank(str(params.get("page"))) ? 1 : Long.parseLong(str(params.get("page")));
        long size = StrUtil.isBlank(str(params.get("size"))) ? 10 : Long.parseLong(str(params.get("size")));
        QueryWrapper<T> wrapper = buildWrapper(params);
        IPage<T> result = service.page(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(result));
    }

    @Operation(summary = "列表查询")
    @GetMapping("/list")
    public Result<List<T>> list(@RequestParam Map<String, Object> params) {
        return Result.ok(service.list(buildWrapper(params)));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<T> detail(@PathVariable Long id) {
        return Result.ok(service.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Long> save(@RequestBody T entity) {
        beforeSave(entity);
        service.save(entity);
        return Result.ok(getId(entity));
    }

    @Operation(summary = "修改")
    @PutMapping
    public Result<Boolean> update(@RequestBody T entity) {
        beforeUpdate(entity);
        return Result.ok(service.updateById(entity));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.ok(service.removeById(id));
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    public Result<Boolean> removeBatch(@RequestBody List<Long> ids) {
        return Result.ok(service.removeByIds(ids));
    }

    /** 保存前钩子 */
    protected void beforeSave(T entity) {
    }

    /** 更新前钩子 */
    protected void beforeUpdate(T entity) {
    }

    /** 记录操作日志等扩展 */
    protected Long getId(T entity) {
        try {
            Field f = fieldCache.get("id");
            if (f != null) {
                f.setAccessible(true);
                Object v = f.get(entity);
                return v == null ? null : Long.valueOf(String.valueOf(v));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 根据请求参数构建查询条件
     */
    protected QueryWrapper<T> buildWrapper(Map<String, Object> params) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (params == null) {
            return wrapper;
        }
        for (Map.Entry<String, Object> e : params.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if (RESERVED.contains(key) || StrUtil.isBlank(str(value))) {
                continue;
            }
            boolean like = key.endsWith("Like");
            String fieldName = like ? key.substring(0, key.length() - 4) : key;
            Field field = fieldCache.get(fieldName);
            if (field == null) {
                continue;
            }
            String column = StrUtil.toUnderlineCase(fieldName);
            Object converted = convert(field, value);
            if (like) {
                wrapper.like(column, converted);
            } else {
                wrapper.eq(column, converted);
            }
        }
        String beginTime = str(params.get("beginTime"));
        String endTime = str(params.get("endTime"));
        boolean hasTimeField = fieldCache.containsKey("createTime");
        if (hasTimeField && StrUtil.isNotBlank(beginTime)) {
            wrapper.ge("create_time", beginTime);
        }
        if (hasTimeField && StrUtil.isNotBlank(endTime)) {
            wrapper.le("create_time", endTime);
        }
        String orderBy = str(params.get("orderBy"));
        if (StrUtil.isNotBlank(orderBy)) {
            String column = StrUtil.toUnderlineCase(orderBy);
            boolean asc = !"false".equalsIgnoreCase(str(params.get("asc")));
            wrapper.orderBy(true, asc, column);
        } else {
            wrapper.orderByDesc(fieldCache.containsKey("createTime") ? "create_time" : "id");
        }
        return wrapper;
    }

    private Object convert(Field field, Object value) {
        Class<?> t = field.getType();
        String s = str(value);
        try {
            if (t == Integer.class) {
                return Integer.valueOf(s);
            }
            if (t == Long.class) {
                return Long.valueOf(s);
            }
            if (t == BigDecimal.class) {
                return new BigDecimal(s);
            }
            if (t == Boolean.class) {
                return Boolean.valueOf(s);
            }
        } catch (Exception ignored) {
        }
        return s;
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

}
