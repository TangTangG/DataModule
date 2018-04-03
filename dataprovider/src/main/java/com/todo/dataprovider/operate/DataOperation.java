package com.todo.dataprovider.operate;


import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataContext;

/**
 *
 * @author TCG
 * @date 2017/8/9
 */
@FunctionalInterface
public interface DataOperation {

    /**
     * Do real action about cur operation.
     *
     * @param callback the callback of this operation
     * @return boolean
     */
    boolean op(DataContext context,DataCallback callback);


}
