# DataModule
Android data-module.Contains http,database and cache.

<h1 align="center">
	<br>
	<img src="https://www.areahash.com/wp-content/uploads/2018/03/data-module.png" alt="data-module">
	<br>
	<br>
	<br>
</h1>

## Introduction
This is a data module based on Android.</br>
Thogh this module,you can operate data(no matter http\db\cache) in same way.</br>
Simple use as fllows:
```Java
DataService.obtain().http(HttpApi.USER_TEST,Action.HTTP_GET)
        .exec(new DataCallback() {
            @Override
            public void onResult(int state, Clause clause, Object data) {
                //success result
            }

            @Override
            public void onError(int state, Clause clause) {
                //failed
                String msg = clause.getMsg();
                //You can get error msg by clause.
            }
        });
```

## Plan
Add service pool in next version.
