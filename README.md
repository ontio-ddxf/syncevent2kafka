# syncevent2kafka
sync event to kafka

##清空之前同步的块高记录开关
```
clear.sync.switch=true
每次启动都会删除索引，从最新块开始同步

clear.sync.switch=false
每次启动从上次结束的高度开始同步，若没有索引则新建索引，从0开始同步
```


##同步之前块高开关
```
sync.preblock.switch=true
若每次从最新块开始同步，则会从0开始同步之前块

sync.preblock.switch=false
不同步之前的区块事件
```
