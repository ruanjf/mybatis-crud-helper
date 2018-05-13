package com.runjf.mybatis.crud;

import com.github.pagehelper.PageHelper;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.select.SimpleSortSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        if (func != null) {
            SortSpecification[] orders = buildSortSpecifications(pageable.getSort());
            com.github.pagehelper.Page<T> list = PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize())
                    .doSelectPage(() -> func.apply(orders));
            return new PageImpl<>(list, pageable, list.getTotal());
        }
        return null;
    }

    public static SortSpecification[] buildSortSpecifications(Sort sort) {
        List<SortSpecification> orders = new ArrayList<>();
        if (sort.isSorted()) {
            sort.map(order -> {
                SortSpecification of = SimpleSortSpecification.of(order.getProperty());
                if (order.isDescending()) {
                    of = of.descending();
                }
                return of;
            }).forEach(orders::add);
        }
        return orders.isEmpty() ? new SortSpecification[0] : orders.toArray(new SortSpecification[orders.size()]);
    }
}
