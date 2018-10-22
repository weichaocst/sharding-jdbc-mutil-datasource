# sharding-jdbc多数据源配置
- 该项目的多数据源并不是配置两个sharding-jdbc数据源，而是配置一个sharding-jdbc数据源和一个普通的数据源
- 采用durid连接池
- sharding-jdbc分片算法采用一致性hash算法
- 本例中不同的数据源对应不同的数据库，但没进行分库操作，可改为使用同一数据库
- 主要用于在不耽误项目正常开发的情况下使用sharding-jdbc对项目进行改造
