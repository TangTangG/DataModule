package com.todo.dataprovider.provider;


import android.support.annotation.Nullable;

import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.annotation.ProviderRegister;
import com.todo.dataprovider.clause.ClauseInfo;
import com.todo.dataprovider.operate.DataOperation;
import com.todo.dataprovider.operate.CacheOperation;
import com.todo.dataprovider.operate.DBDataOperation;
import com.todo.dataprovider.operate.HttpOperation;


/**
 *
 * @author TCG
 * @date 2017/8/9
 */
@ProviderRegister(target = "@GuTang.base")
public class DefaultProvider extends BaseDataProvider {

    @Override
    public DataOperation dispatchAction(DataService.Clause clause, DataCallback callback) {
        return switchOperation(clause);
    }

    @Nullable
    private DataOperation switchOperation(DataService.Clause clause) {
        ClauseInfo clauseInfo = clause.getClauseInfo();
        switch (clauseInfo._type) {
            case HTTP:
                return new HttpOperation(clause);
            case CACHE:
                return new CacheOperation(clause);
            case DB:
                return new DBDataOperation(clause);
            default:
                return null;
        }
    }

}
