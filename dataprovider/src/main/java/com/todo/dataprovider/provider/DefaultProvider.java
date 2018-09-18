package com.todo.dataprovider.provider;


import android.support.annotation.Nullable;

import com.todo.autocollect.annotation.ProviderRegister;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.annotation.ProviderAction;
import com.todo.dataprovider.service.Clause;
import com.todo.dataprovider.service.ClauseInfo;
import com.todo.dataprovider.operate.DataOperation;
import com.todo.dataprovider.operate.CacheOperation;
import com.todo.dataprovider.operate.DBDataOperation;
import com.todo.dataprovider.http.HttpOperation;


/**
 *
 * @author TCG
 * @date 2017/8/9
 */
@ProviderAction
@ProviderRegister(type = "@GuTang.base",target = BaseDataProvider.class)
public class DefaultProvider extends BaseDataProvider {

    @Override
    public DataOperation dispatchAction(Clause clause, DataCallback callback) {
        return switchOperation(clause);
    }

    @Nullable
    private DataOperation switchOperation(Clause clause) {
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
