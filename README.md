# Sharding-JDBC 接入文档  
##一、引入shardingsphere maven依赖  
```$xslt
        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-jdbc-core</artifactId>
            <version>3.0.0.M4</version>
        </dependency>

        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-core</artifactId>
            <version>3.0.0.M4</version>
        </dependency>
```
 	
##二、配置普通数据源和Sharding-JDBC数据源
同时配置普通数据源和sharding数据源的目的是在使用sharding-JDBC改造项目时可以分模块逐步改造，已改造完成的模块使用sharding数据源，未改造的模块使用普通JDBC数据源  
###1.配置主数据源（普通数据源）
```$xslt
@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = MasterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig {

    // 配置使用该数据源的包和sql
    static final String PACKAGE = "org.spring.springboot.dao.master";
    static final String MAPPER_LOCATION = "classpath:mapper/master/*.xml";

    @Value("${master.datasource.url}")
    private String url;

    @Value("${master.datasource.username}")
    private String user;

    @Value("${master.datasource.password}")
    private String password;

    @Value("${master.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }

    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MasterDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
```  

普通数据源作为master数据源，配置master数据源的作用范围，然后配置master数据源事务和SqlSessionFactory   
- @Primary //该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让autowire注解报错，官网要求当多个数据源时，必须指定一个datasource，另一个datasource则不用添加。
- @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实例的一个注入（例如有多个DataSource类型的实例）。
- @MapperScan(basePackages =MasterDataSourceConfig.PACKAGE, sqlSessionTemplateRef = masterSqlSessionFactory)  basePackages为mapper所在的包，sqlSessionTemplateRef要引用的实例。
###2.配置采用shardingsphere的分片数据源
```$xslt
@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = ShardingDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "clusterSqlSessionFactory")
public class ShardingDataSourceConfig {

    // 精确到 cluster 目录，以便跟其他数据源隔离
    static final String PACKAGE = "org.spring.springboot.dao.cluster";
    static final String MAPPER_LOCATION = "classpath:mapper/cluster/*.xml";

    @Value("${cluster.datasource.url}")
    private String url;

    @Value("${cluster.datasource.username}")
    private String user;

    @Value("${cluster.datasource.password}")
    private String password;

    @Value("${cluster.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "clusterDataSource")
    public DataSource clusterDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        Properties properties = new Properties();
        //打开真实SQL打印
        properties.setProperty("sql.show","true");
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new HashMap<String, Object>(), properties);
    }

    //配置Order表分片规则
    public TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable("t_order");
        //配置分片节点
        result.setActualDataNodes("demo_ds.t_order_${[0, 1]}");
        //配置分片算法
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_id", new ConsistentHashingPreciseShardingAlgorithmSharding()));
        return result;
    }
    
    //配置OrderItem表分片规则
    public TableRuleConfiguration getOrderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable("t_order_item");
        result.setActualDataNodes("demo_ds.t_order_item_${[0, 1]}");
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_id", new ConsistentHashingPreciseShardingAlgorithmSharding()));
        return result;
    }

    public Map<String, DataSource> createDataSourceMap() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        Map<String, DataSource> result = new HashMap<>();
        result.put("demo_ds",dataSource);
        return result;
    }


    @Bean(name = "clusterTransactionManager")
    public DataSourceTransactionManager clusterTransactionManager() throws SQLException {
        return new DataSourceTransactionManager(clusterDataSource());
    }

    @Bean(name = "clusterSqlSessionFactory")
    public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("clusterDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(ShardingDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}

```
在开发中，可以将sql.show设为true,打开sharding-jdbc的SQL语句调试功能，可以显示逻辑SQL和物理SQL。  
以上shardingsphere数据源只配置了分表，并设置了分表策略，分库和主从配置可参考Sharding-Sphere官方文档  
- 注：在多数据源环境下，使用master数据源可以不指定事务，直接在service上加@Transactional(rollbackFor = Exception.class)即可，  使用从数据源（sharding数据源）时，要指定使用的事务名称
@Transactional(transactionManager = "clusterTransactionManager",rollbackFor = Exception.class)

## 三、配置分片策略
```$xslt
/**
 * @author weichao
 * @Description 单分片健一致性哈希算法分片设置
 * @date 2018/10/18 9:39
 */
public class ConsistentHashingPreciseShardingAlgorithmSharding implements PreciseShardingAlgorithm<String>  {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        //由一致性hash算法获取表节点
        MurmurConsistHash<String> consistentHash = new MurmurConsistHash<>(availableTargetNames);
        for (String each : availableTargetNames) {
            if (each.endsWith(consistentHash.get(shardingValue.getValue().toString()))) {
                //System.out.println("当前访问节点为："+consistentHash.get(shardingValue.getValue().toString())+"分片健值为："+shardingValue.getColumnName()+":"+shardingValue.getValue().toString());
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }

}
```
根据公司业务，采用单分片策略，分片算法采用一致性hash算法，如果要采用多分片策略，实现ComplexKeysShardingAlgorithm接口并覆盖doSharding方法即可  

**附：一致性hash算法代码：**
```$xslt
public class MurmurConsistHash<T> {
    // 每个机器节点关联的虚拟节点个数
    private int numberOfReplicas;
    // 环形虚拟节点
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();
    private final String VERTUAL_PRFIX = "$";

    public MurmurConsistHash(Collection<T> nodes) {
        this(100, nodes);
    }

    /**
     * @param numberOfReplicas 每个机器节点关联的虚拟节点个数
     * @param nodes            真实机器节点
     */
    public MurmurConsistHash(int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    /**
     * 增加真实机器节点
     *
     * @param node
     */
    public void add(T node) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            circle.put(murMurHash(node.toString() + VERTUAL_PRFIX + i), node);
        }
    }

    /**
     * 删除真实机器节点
     *
     * @param node
     */
    public void remove(T node) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            circle.remove(murMurHash(node.toString() + VERTUAL_PRFIX + i));
        }
    }

    /**
     * 取得真实机器节点
     *
     * @param key
     * @return
     */
    public T get(String key) {
        if (circle.isEmpty()) {
            return null;
        }

        long hash = murMurHash(key);
        if (!circle.containsKey(hash)) {
            // 沿环的顺时针找到一个虚拟节点
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        // 返回该虚拟节点对应的真实机器节点的信息
        return circle.get(hash);
    }

    private static Long murMurHash(String key) {
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }
}
```
一致性hash算法了解：朱双印博客 http://www.zsythink.net/archives/1182/  


## 四、配置文件
application.properties
```$xslt
server.port=8001
## master 数据源配置
master.datasource.url=jdbc:mysql://localhost:3306/springbootdb?useUnicode=true&characterEncoding=utf8
master.datasource.username=root
master.datasource.password=123456
master.datasource.driverClassName=com.mysql.jdbc.Driver

## cluster 数据源配置
cluster.datasource.url=jdbc:mysql://localhost:3306/springbootdb?useUnicode=true&characterEncoding=utf8
cluster.datasource.username=root
cluster.datasource.password=123456
cluster.datasource.driverClassName=com.mysql.jdbc.Driver
```
- 现在两个数据源配置的同一个数据库，也可根据实际业务需求配置不同数据库

## 五、sharding-jdbc-mutil-datasource使用说明
### 1.普通数据源接口
http://localhost:8001/api/user  数据查询接口，SQL采用sharding-jdbc不支持的多子查询
http://localhost:8001/api/addUser 插入数据接口 主要用来测试事务有效性

### 2.sharding数据源接口
http://localhost:8001/demo/insert 插入数据接口，用来测试分库分表，local事务，表绑定，SQL语句优化功能  
http://localhost:8001/demo/page 分页优化测试接口  

