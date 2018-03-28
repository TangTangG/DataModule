package com.todo.dataprovider.operate;


import com.todo.dataprovider.DataCallback;

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
    boolean op(DataCallback callback);


}
