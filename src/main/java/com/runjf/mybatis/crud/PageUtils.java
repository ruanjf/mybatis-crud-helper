package com.runjf.mybatis.crud;

import com.github.pagehelper.PageHelper;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.select.SimpleSortSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 *
 * Created by rjf on 2018/5/13.
 */
public class PageUtils {

    public static <T> Page<T> buildPage(Pageable pageable, Function<SortSpecification[], List<T>> func) {
        return buildPage(pageable, func, null);
    }

    public static <T> Page<T> buildPage(Pageable pageable, Function<SortSpecification[], List<T>> func, SqlTable sqlTable) {
        if (func != null) {
            SortSpecification[] orders = buildSortSpecifications(pageable.getSort(), sqlTable);
            //noinspection unchecked
            List<T>[] result = new List[1];
            com.github.pagehelper.Page<T> list = PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize())
                    .doSelectPage(() -> {
                        List<T> apply = func.apply(orders);
                        if (apply != null && ! (apply instanceof com.github.pagehelper.Page)) {
                            result[0] = apply;
                        }
                    });
            return new PageImpl<>(result[0] != null ? result[0] : list, pageable, list.getTotal());
        }
        return null;
    }

    public static SortSpecification[] buildSortSpecifications(Sort sort) {
        return buildSortSpecifications(sort, null);
    }

    public static SortSpecification[] buildSortSpecifications(Sort sort, SqlTable sqlTable) {
        List<SortSpecification> orders = new ArrayList<>();
        if (sort.isSorted()) {
            Class<? extends SqlTable> sqlTableClass = sqlTable != null ? sqlTable.getClass() : null;
            sort.map(order -> {
                SortSpecification of = null;
                if (sqlTableClass != null) {
                    try {
                        Field field = sqlTableClass.getField(order.getProperty());
                        of = (SortSpecification) field.get(sqlTable);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                } else {
                    of = SimpleSortSpecification.of(order.getProperty());

                }
                if (of != null && order.isDescending()) {
                    of = of.descending();
                }
                return of;
            }).forEach(orders::add);
        }
        return orders.isEmpty() ? new SortSpecification[0] : orders.toArray(new SortSpecification[orders.size()]);
    }
}
