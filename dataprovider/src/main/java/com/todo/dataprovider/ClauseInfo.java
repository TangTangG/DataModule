package com.todo.dataprovider;

import android.util.Log;


import com.todo.dataprovider.OperateType;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by TCG on 2017/8/9.
 */

public class ClauseInfo {
    public String _target;
    public OperateType _type;
    public LinkedHashMap<String,Condition> conditions = new LinkedHashMap<>();

    public ClauseInfo(OperateType type, String _target) {
        this._target = _target;
        this._type = type;
    }

    public class Condition{
        public String key;
        public String value = null;
        public String[] values = null;
        public Object obj = null;

        public Condition(String key) {
            this.key = key;
        }

        public void is(String val){
            this.value = val;
            update();
        }

        public void is(Object val){
            this.obj = val;
            update();
        }

        public void is(String... val){
            this.values = val;
            update();
        }

        private void update() {
            Condition condition = conditions.get(key);
            if (condition != null){
                conditions.remove(key);
            }
            conditions.put(key,this);
        }
    }

    public void addCondition(String key) {
        Condition condition = conditions.get(key);
        if (condition != null){
            conditions.remove(key);
        }
        condition = new Condition(key);
        conditions.put(key,condition);
    }

    Condition getInitCondition(){
        Collection<Condition> values = conditions.values();
        int size = values.size();
        Condition[] conditionA = new Condition[size];
        values.toArray(conditionA);
        int length = conditionA.length;
        if (length > 0){
            return conditionA[length - 1];
        } else {
            Log.d("ClauseInfo", "getInitCondition: no condition exist,mast apply method where");
        }
        return null;
    }

    public Condition getCondition(String key){
        Condition condition = conditions.get(key);
        if (condition == null){
            Log.w("ClauseInfo", "getCondition: no such condition exist,mast apply method where ---->"+key);
        }
        return condition;
    }

    public LinkedHashMap<String, Condition> getConditions() {
        return new LinkedHashMap<>(conditions);
    }
}
