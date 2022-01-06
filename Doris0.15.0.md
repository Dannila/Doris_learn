# Doris

## 一、编译Doris

主要介绍如何通过源码编译 Doris。

### 1）、使用Docker开发镜像编译

编译案例：镜像   apache/incubator-doris:build-env-for-0.15.0 来编译Doris0.15.0版本

#### 1、下载Docker镜像

```
docker pull apache/incubator-doris:build-env-for-0.15.0
```

检查镜像下载完成

```
docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             SIZE
apache/incubator-doris  build-env-for-0.15.0    49f68cecbc1a     4 days ago      3.76GB
```

注1：针对不同的 Doris 版本，需要下载对应的镜像版本。从 Apache Doris 0.15 版本起，后续镜像版本号将与 Doris 版本号统一。比如可以使用 apache/incubator-doris:build-env-for-0.15.0 来编译 0.15.0 版本。

注2：apache/incubator-doris:build-env-latest 用于编译最新主干版本代码，会随主干版本不断更新。可以查看 docker/README.md 中的更新时间。

| 镜像版本                                    | commit id                                                    | doris 版本      |
| ------------------------------------------- | ------------------------------------------------------------ | --------------- |
| apache/incubator-doris:build-env            | before [ff0dd0d(opens new window)](https://github.com/apache/incubator-doris/commit/ff0dd0d2daa588f18b6db56f947e813a56d8ec81) | 0.8.x, 0.9.x    |
| apache/incubator-doris:build-env-1.1        | [ff0dd0d(opens new window)](https://github.com/apache/incubator-doris/commit/ff0dd0d2daa588f18b6db56f947e813a56d8ec81) | 0.10.x, 0.11.x  |
| apache/incubator-doris:build-env-1.2        | [4ef5a8c(opens new window)](https://github.com/apache/incubator-doris/commit/4ef5a8c8560351d7fff7ff8fd51c4c7a75e006a8) | 0.12.x - 0.14.0 |
| apache/incubator-doris:build-env-1.3.1      | [ad67dd3(opens new window)](https://github.com/apache/incubator-doris/commit/ad67dd34a04c1ca960cff38e5b335b30fc7d559f) | 0.14.x          |
| apache/incubator-doris:build-env-for-0.15.0 | [a81f4da (opens new window)](https://github.com/apache/incubator-doris/commit/a81f4da4e461a54782a96433b746d07be89e6b54)or later | 0.15.0          |
| apache/incubator-doris:build-env-latest     | trunk                                                        | trunk           |

#### 2、下载源码

```
wget https://dist.apache.org/repos/dist/dev/incubator/doris/xxx.tar.gz
```

建议直接下载好源码放入本地目录并解压

#### 3、运行镜像

```
docker run -it -v /root/.m2:/root/.m2 -v /home/deploy/apache-doris-0.15.0-incubating-src/:/home/deploy/apache-doris-0.15.0-incubating-src/ apache/incubator-doris:build-env-for-0.15.0
```

注1：建议将镜像中 maven 的 `.m2` 目录挂载到宿主机目录，以防止每次启动镜像编译时，重复下载 maven 的依赖库

 `/your/local/incubator-doris-DORIS-x.x.x-release/`: 为本地源代码地址

 `/root/incubator-doris-DORIS-x.x.x-release/ ` ： 挂载的镜像内地址

注2：从 build-env-1.3.1 的docker镜像起，同时包含了 OpenJDK 8 和 OpenJDK 11，并且默认使用 OpenJDK 11 编译。请确保编译使用的 JDK 版本和运行时使用的 JDK 版本一致，否则会导致非预期的运行错误。可以使用在进入编译镜像的容器后，使用以下命令切换默认 JDK 版本

切换到JDK8

```
$ alternatives --set java java-1.8.0-openjdk.x86_64
$ alternatives --set javac java-1.8.0-openjdk.x86_64
$ export JAVA_HOME=/usr/lib/jvm/java-1.8.0
```

切换到JDK11：

```
$ alternatives --set java java-11-openjdk.x86_64
$ alternatives --set javac java-11-openjdk.x86_64
$ export JAVA_HOME=/usr/lib/jvm/java-11
```

#### 4、编译Doris

进入镜像内源码目录然后启动编译脚本

```
sh build.sh
```

注：如果使用 `build-env-for-0.15.0` 或之后的版本，第一次编译的时候要使用如下命令：

```
sh build.sh --clean --be --fe --ui
```

这是因为 build-env-for-0.15.0 版本镜像升级了 thrift(0.9 -> 0.13)，需要通过 --clean 命令强制使用新版本的 thrift 生成代码文件，否则会出现不兼容的代码。

编译完成后，产出文件在 `output/` 目录中。

### 2）、直接编译Doris

#### 1、下载doris源码

```
wget https://mirrors.bfsu.edu.cn/apache/incubator/doris/0.13.0-incubating/apache-doris-0.15.0-incubating-src.tar.gz
```

建议直接下载好源码放入本地目录并解压

#### 2、升级GCC

```
yum install gcc-c++
wget http://ftp.tsukuba.wide.ad.jp/software/gcc/releases/gcc-7.3.0/gcc-7.3.0.tar.gz
tar zxvf tar zxvf gcc-7.3.0.tar.gz
cd gcc-7.3.0
yum install lbzip2
./contrib/download_prerequisites
mkdir build
cd build/
../configure --enable-checking=release --enable-languages=c,c++ --disable-multilib
make -j 40 && make install
```

#### 3、升级CMAKE

```
wget https://cmake.org/files/v3.6/cmake-3.6.2.tar.gz
tar xvf cmake-3.6.2.tar.gz && cd cmake-3.6.2/
./bootstrap
gmake -j 40 && gmake install
```

注：如遇到以下报错

![](D:\文档资料\doris\doris文档\图片\C467D3D0-FB70-4d48-9A6A-3027C4562053.png)

则

```
rm -f CMakeCache.txt
yum -y install ncurses-devel
yum install openssl-devel
```

#### 4、升级flex（2.6.0版本以上）

```
https://github.com/westes/flex/releases
tar -zxvf *.tar.gz
cd *
./configure
make -j 40 && make install
```

#### 5、编译doris

```
sh build.sh
```

#### 6、手动编译过程中可能会遇到的问题

6.1. 报错下载jar失败

![](D:\文档资料\doris\doris文档\图片\D82F4924-340F-40dc-937D-8D04DB361ED0.png)

下载对应的jar包放入`/home/deploy/incubator-doris-master/thirdparty/src`目录下

6.2. 报错找不到libfl.so.2文件

执行命令：find / -name libfl.so.2

找到该文件所在位置后将该路径添加到/etc/ld.so.conf文件中

添加后执行：ldconfig

```
find / -name libfl.so.2
vim /etc/ld.so.conf
ldconfig
```



## 二、Doris的安装部署

安装Doria之前请先根据编译文档编译 Doris

官网地址：https://doris.apache.org/master/zh-CN/installing/install-deploy.html#%E8%BD%AF%E7%A1%AC%E4%BB%B6%E9%9C%80%E6%B1%82

####  Linux 操作系统版本需求

| Linux 系统 | 版本         |
| ---------- | ------------ |
| CentOS     | 7.1 及以上   |
| Ubuntu     | 16.04 及以上 |

#### [#](https://doris.apache.org/master/zh-CN/installing/install-deploy.html#软件需求)软件需求

| 软件 | 版本         |
| ---- | ------------ |
| Java | 1.8 及以上   |
| GCC  | 4.8.2 及以上 |

#### 开发测试环境

| 模块     | CPU  | 内存  | 磁盘                 | 网络     | 实例数量 |
| -------- | ---- | ----- | -------------------- | -------- | -------- |
| Frontend | 8核+ | 8GB+  | SSD 或 SATA，10GB+ * | 千兆网卡 | 1        |
| Backend  | 8核+ | 16GB+ | SSD 或 SATA，50GB+ * | 千兆网卡 | 1-3 *    |

#### [#](https://doris.apache.org/master/zh-CN/installing/install-deploy.html#生产环境)生产环境

| 模块     | CPU   | 内存  | 磁盘                     | 网络     | 实例数量（最低要求） |
| -------- | ----- | ----- | ------------------------ | -------- | -------------------- |
| Frontend | 16核+ | 64GB+ | SSD 或 RAID 卡，100GB+ * | 万兆网卡 | 1-5 *                |
| Backend  | 16核+ | 64GB+ | SSD 或 SATA，100G+ *     | 万兆网卡 | 10-100 *             |

#### Broker 部署

Broker 是用于访问外部数据源（如 hdfs）的进程。通常，在每台机器上部署一个 broker 实例即可。

#### [#](https://doris.apache.org/master/zh-CN/installing/install-deploy.html#网络需求)网络需求

Doris 各个实例直接通过网络进行通讯。以下表格展示了所有需要的端口

| 实例名称 | 端口名称               | 默认端口 | 通讯方向                     | 说明                                                 |
| -------- | ---------------------- | -------- | ---------------------------- | ---------------------------------------------------- |
| BE       | be_port                | 9060     | FE --> BE                    | BE 上 thrift server 的端口，用于接收来自 FE 的请求   |
| BE       | webserver_port         | 8040     | BE <--> BE                   | BE 上的 http server 的端口                           |
| BE       | heartbeat_service_port | 9050     | FE --> BE                    | BE 上心跳服务端口（thrift），用于接收来自 FE 的心跳  |
| BE       | brpc_port*             | 8060     | FE<-->BE, BE <--> BE         | BE 上的 brpc 端口，用于 BE 之间通讯                  |
| FE       | http_port *            | 8030     | FE <--> FE，用户             | FE 上的 http server 端口                             |
| FE       | rpc_port               | 9020     | BE --> FE, FE <--> FE        | FE 上的 thrift server 端口，每个fe的配置需要保持一致 |
| FE       | query_port             | 9030     | 用户                         | FE 上的 mysql server 端口                            |
| FE       | edit_log_port          | 9010     | FE <--> FE                   | FE 上的 bdbje 之间通信用的端口                       |
| Broker   | broker_ipc_port        | 8000     | FE --> Broker, BE --> Broker | Broker 上的 thrift server，用于接收请求              |

#### 手动部署

(1) FE 部署

1、拷贝 FE 部署文件到指定节点

将源码编译生成的 output 下的 fe 文件夹拷贝到 FE 的节点指定部署路径下并进入该目录。

2、配置 FE

1. 配置文件为 conf/fe.conf。其中注意：`meta_dir`是元数据存放位置。默认值为 `${DORIS_HOME}/doris-meta`。需**手动创建**该目录。

   **注意：生产环境强烈建议单独指定目录不要放在Doris安装目录下，最好是单独的磁盘（如果有SSD最好），测试开发环境可以使用默认配置**

2. fe.conf 中 JAVA_OPTS 默认 java 最大堆内存为 4GB，**建议生产环境调整至 8G 以上**

3、启动FE

到fe的bin目录启动 

```
./start_fe.sh --daemon 
```

如果成功的话，jps有进程，有其他问题可以查看fe目录下log里面的fe.log，

（2）BE部署

1、连接FE

```
mysql -h 127.0.0.1 -P 9030 -uroot
```

注1：这里的ip是内网ip，如果不知道的，输入ifconfig看一下。
2、然后在SQL界面添加BE

```
ALTER SYSTEM ADD BACKEND "10.190.11.2:9050";
```

然后退出。

3、启动BE

在be文件夹下面创建storage文件夹 mkdir  /data/doris/be/storage

到be文件夹的bin目录 

```
./start_be.sh --daemon
```

(4)验证

重新连接

```
mysql -h 10.190.11.2 -P 9030 -uroot

SHOW PROC '/backends';
```

看一下Alive那边是不是true，不是的话看看日志。


## 三、Doris导入深度调研

### 1）、Spark Doris Connector

Spark Doris Connector 可以支持通过 Spark 读取 Doris 中存储的数据，也支持通过Spark写入数据到Doris。

#### 1、版本兼容

| Connector | Spark | Doris  | Java | Scala |
| --------- | ----- | ------ | ---- | ----- |
| 1.0.0     | 2.x   | 0.12+  | 8    | 2.11  |
| 1.0.0     | 3.x   | 0.12.+ | 8    | 2.12  |

#### 2、编译与安装

在 `extension/spark-doris-connector/` 源码目录下执行：

注1：这里如果你没有整体编译过 doris 源码，需要首先编译一次 Doris 源码，不然会出现 thrift 命令找不到的情况，需要到 `incubator-doris` 目录下执行 `sh build.sh`。

注2：建议在 doris 的 docker 编译环境 `apache/incubator-doris:build-env-1.2` 下进行编译，因为 1.3 下面的JDK 版本默认是JDK11，会存在编译问题。所以从 build-env-1.3.1 的docker镜像起注意切换JDK版本。

```
sh build.sh 3  ## spark 3.x版本，默认是3.1.2
sh build.sh 2  ## spark 2.x版本，默认是2.3.4
```

注：我们视博环境spark是3.1.1，所以要在`pom_3.0.xml`中修改spark版本。

编译成功后，会在 `output/` 目录下生成文件 `doris-spark-1.0.0-spark-3.1.2_2.12.jar`。将此文件复制到 `Spark` 的 `ClassPath` 中即可使用 `Spark-Doris-Connector`。

#### 3、spark3.x版本编译可能会遇到的问题

sh build.sh 时如果报错

```
[ERROR] /home/deploy/apache-doris-0.15.0-incubating-src/extension/spark-doris-connector/src/main/scala/org/apache/doris/spark/sql/DorisSourceProvider.scala:24: error: object v2 is not a member of package org.apache.spark.sql.sources
[ERROR] import org.apache.spark.sql.sources.v2.writer.streaming.StreamWriter
[ERROR]                                     ^
[ERROR] /home/deploy/apache-doris-0.15.0-incubating-src/extension/spark-doris-connector/src/main/scala/org/apache/doris/spark/sql/DorisSourceProvider.scala:25: error: object v2 is not a member of package org.apache.spark.sql.sources
[ERROR] import org.apache.spark.sql.sources.v2.{DataSourceOptions, StreamWriteSupport}
[ERROR]                                     ^
[ERROR] /home/deploy/apache-doris-0.15.0-incubating-src/extension/spark-doris-connector/src/main/scala/org/apache/doris/spark/sql/DorisSourceProvider.scala:37: error: not found: type StreamWriteSupport
[ERROR] private[sql] class DorisSourceProvider extends DataSourceRegister with RelationProvider with CreatableRelationProvider with StreamWriteSupport with Logging {
......
```

原因：v2 包不在 spark3 中

参考连接：

[1]: SparkDorisConnector构建失败，spark3	"https://github.com/apache/incubator-doris/issues/7363"

问题详解：部分 Spark 连接器代码是使用“Spark DatasourcesV2”API 实现的。但是，“Spark DatasourcesV2”API 仅存在于 spark 2.3.x/2.4.x 中，不存在于 spark 2.1.x/2.2.x/3.x 中。在 spark 3.x 之后，该 API 似乎已被删除。

参考链接：

[2]: Spark连接器支持多个Spark版本：2.1.x/2.3.x/3.x	"https://github.com/apache/incubator-doris/pull/6956"

在Doris版本中0.15.0版本支持spark读写Doris，但在docker编译的时候存在上述问题，因此手动编译了编译最新主干版本代码，解决了此问题

注：在官网编译部分中提到“`apache/incubator-doris:build-env-latest` 用于编译最新主干版本代码，会随主干版本不断更新。可以查看 `docker/README.md` 中的更新时间。”可能可以解决此问题，暂时没有尝试



### 2）、Flink Doris Connector

Flink Doris Connector 可以支持通过 Flink 读写 Doris 中存储的数据。

#### 1、版本兼容

| Connector | Flink           | Doris  | Java | Scala |
| --------- | --------------- | ------ | ---- | ----- |
| 1.0.0     | 1.11.x , 1.12.x | 0.13+  | 8    | 2.12  |
| 1.0.0     | 1.13.x          | 0.13.+ | 8    | 2.12  |

针对Flink1.13.0 版本适配问题

```
 <properties>
        <scala.version>2.12</scala.version>
        <flink.version>1.11.2</flink.version>
        <libthrift.version>0.9.3</libthrift.version>
        <arrow.version>0.15.1</arrow.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <doris.home>${basedir}/../../</doris.home>
        <doris.thirdparty>${basedir}/../../thirdparty</doris.thirdparty>
    </properties>
```

注：我们视博环境flink是1.13.1，所以要在`pom.xml`中修改flink版本。将这里的 `flink.version` 改成和Flink 集群版本一致，编译即可。

#### 2、编译与安装

在 `extension/flink-doris-connector/` 源码目录下执行：

```
sh build.sh
```

注1：这里如果你没有整体编译过 doris 源码，需要首先编译一次 Doris 源码，不然会出现 thrift 命令找不到的情况，需要到 `incubator-doris` 目录下执行 `sh build.sh`。

注2：建议在 doris 的 docker 编译环境 `apache/incubator-doris:build-env-1.2` 下进行编译，因为 1.3 下面的JDK 版本默认是JDK11，会存在编译问题。所以从 build-env-1.3.1 的docker镜像起注意切换JDK版本。

编译成功后，会在 `output/` 目录下生成文件 `doris-flink-1.0.0-flink-1.13.1_2.12.jar` 。

将此文件复制到 `Flink` 的 `ClassPath` 中即可使用 `Flink-Doris-Connector`。



## 四、Doris使用

### 1）、简单使用

Doris 采用 MySQL 协议进行通信，用户可通过 MySQL client 或者 MySQL JDBC连接到 Doris 集群。选择 MySQL client 版本时建议采用5.1 之后的版本，因为 5.1 之前不能支持长度超过 16 个字符的用户名。

Doris支持支持单分区和复合分区两种建表方式。

#### 1、创表

##### 1.1、创建单分区表

创建一个名字为 table1 的逻辑表。分桶列为 id，桶数为 10。

建表语句如下

```
create table table1(
 id INT,
 name VARCHAR(32),
 age INT
)
DISTRIBUTED BY HASH(id) BUCKETS 10
PROPERTIES("replication_num" = "1");
```

##### 1.2、创建复合分区表

建立一个名字为 table2 的逻辑表。使用 event_day 列作为分区列，建立3个分区: p201706, p201707, p201708，每个分区使用 siteid 进行哈希分桶，桶数为10

建表语句如下

```
CREATE TABLE table2
(
    event_day DATE,
    siteid INT,
    citycode SMALLINT,
    username VARCHAR(32)
)
PARTITION BY RANGE(event_day)
(
    PARTITION p201706 VALUES LESS THAN ('2017-07-01'),
    PARTITION p201707 VALUES LESS THAN ('2017-08-01'),
    PARTITION p201708 VALUES LESS THAN ('2017-09-01')
)
DISTRIBUTED BY HASH(siteid) BUCKETS 10
PROPERTIES("replication_num" = "1");
```

#### 2、流式导入

流式导入通过 HTTP 协议向 Doris 传输数据，可以不依赖其他系统或组件直接导入本地数据。

##### 2.1、单分区表数据导入

本地文件 `table_data` 以 `,` 作为数据之间的分隔，具体内容如下：

```
1,jim,2
2,grace,4
3,tom,8
4,bush,9
5,helen,16
```

以 "label:135" 为 Label，使用本地文件 table_data 导入 table1 表。

```
curl --location-trusted -u root -H "label:135" -H "column_separator:," -T /home/zhongtai/tmp/table_data http://172.31.41.20:8030/api/ds/table1/_stream_load
```

##### 2.2、复合分区表数据导入

本地文件 `test_table2` 以 `,` 作为数据之间的分隔，具体内容如下：

```
2017-07-03,1,1,jim
2017-07-05,2,1,grace 
2017-07-12,3,2,tom
2017-07-15,4,3,bush
2017-07-12,5,3,helen
```

以 "label:129" 为 Label，使用本地文件 test_table2 导入 table2 表。

```
curl --location-trusted -u root -H "label:129" -H "column_separator:," -T /home/zhongtai/tmp/test_table2 http://172.31.41.20:8030/api/ds/table2/_stream_load
```

注1：FE_HOST 是任一 FE 所在节点 IP，8030 为 fe.conf 中的 http_port

注2：采用流式导入建议文件大小限制在 10GB 以内，过大的文件会导致失败重试代价变大

注3：每一批导入数据都需要取一个 Label，Label 最好是一个和一批数据有关的字符串，方便阅读和管理。Doris 基于 Label 保证在一个Database 内，同一批数据只可导入成功一次。失败任务的 Label 可以重用。

注4、流式导入是同步命令。命令返回成功则表示数据已经导入，返回失败表示这批数据没有导入。



#### 2）、离线写入数据到Doris（Spark）

##### 1、通过spark读取hudi数据写入doris

**使用Spark Doris Connector编译的doris-spark-1.0.0-spark-3.1.2_2.12.jar**

1.1、将编译的doris-spark-1.0.0-spark-3.1.2_2.12.jar复制到 `Spark` 的 `ClassPath` 中。

1.2、在doris里创建表

1.3、代码

```
package com.hcdsj

import org.apache.spark.{SparkConf}
import org.apache.spark.sql.SparkSession

object DorisTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark = SparkSession.builder().config(conf).getOrCreate()

    val frame = spark.read.format("hudi").load("hdfs://localhost:9000/hudi/rdbms/default/db_issue_clear/ods_db_issue_clear_tb_nclear_inside_record_dispute/clean_tb")

    frame.write.format("doris").option("doris.fenodes", "localhost:8030").option("doris.table.identifier", "ds.test1").option("user", "root").option("password","").option("mode","Append").save()

    spark.stop()
  }
}
```

1.4、打包

1.5、启动spark submit运行jar 写入数据

```
bin/spark-submit \
    --class com.hcdsj.dorisTest \
    --jars ./jars/hudi-spark3-bundle_2.12-0.9.0.jar,./jars/doris-spark-1.0.0-spark-3.1.1_2.12.jar,./jars/guava-14.0.1.jar,./jars/guava-28.0-jre.jar,./jars/failureaccess-1.0.1.jar \
    --master spark://172.31.41.30:7077 \
    --driver-memory 8g \
    --executor-memory 8g \
    --executor-cores 8 \
    --conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
/home/zhongtai/tmp/HdfsToDoris-1.0-SNAPSHOT.jar
```



##### 2、通过spark读取mysql的数据写入doris

**使用jdbc的连接方式读取MySQL数据并且写入doris**

2.1、在Doris里创建表

2.2、代码

```
package com.hcdsj

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object JdbcDoris {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val spark = SparkSession.builder().config(conf).getOrCreate()
    val df: DataFrame = spark.read.format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://172.16.2.120:3306")
      .option("dbtable", "db_issue_clear.tb_nclear_dispute_setting")
      .option("user", "root")
      .option("password", "123456")
      .load()
 
    df.write.format("jdbc").option("url","jdbc:mysql://172.31.41.20:9030/ds").option("user","root").option("dbtable","ods_etc_project_etcobubusiness").save()

    spark.stop()

  }
}
```

2.3、打包

2.4启动spark submit运行jar 写入数据

```
bin/spark-submit \
    --class com.hcdsj.JdbcDoris \
    --jars ./jars/hudi-spark3-bundle_2.12-0.9.0.jar,./jars/mysql-connector-java-5.1.34.jar,./jars/doris-spark-1.0.0-spark-3.1.1_2.12.jar,./jars/guava-14.0.1.jar,./jars/guava-28.0-jre.jar,./jars/failureaccess-1.0.1.jar \
    --master spark://172.31.41.30:7077 \
    --driver-memory 8g \
    --executor-memory 8g \
    --executor-cores 8 \
    --conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
/home/zhongtai/tmp/HdfsToDoris-1.0-SNAPSHOT.jar
```



#### 3）、实时写入数据到Doris（Spark）

##### 使用doris-spark-1.0.0-spark-3.1.2_2.12.jar通过spark实时读取hudi数据写入doris

代码

```
package com.hcdsj

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkDorisTest {

  def main(args: Array[String]): Unit = {

    val sc = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark = SparkSession.builder().config(sc).getOrCreate()

   val frame = spark.readStream.format("hudi").load("/tmp/hudi/my_hudi_table")

   frame.writeStream.format("doris").option("doris.table.identifier", "ds.test1").option("doris.fenodes", "172.31.41.20:8030").option("user", "root").option("password","").option("checkpointLocation", "/tmp/zhongtai/sq/t1_check").option("mode","Append").start().awaitTermination()
    
    spark.stop()

  }
}

```

启动spark submit运行jar 写入数据

```
bin/spark-submit \
    --class com.hcdsj.SparkDorisTest \
    --jars ./jars/hudi-spark3-bundle_2.12-0.9.0.jar,./jars/doris-spark-1.0.0-spark-3.1.1_2.12.jar,./jars/guava-14.0.1.jar,./jars/guava-28.0-jre.jar,./jars/failureaccess-1.0.1.jar \
    --master spark://172.31.41.30:7077 \
    --driver-memory 8g \
    --executor-memory 8g \
    --executor-cores 8 \
    --conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
/home/zhongtai/tmp/HdfsToDoris-1.0-SNAPSHOT.jar
```

注意：动态获取数据的dataframe中的Schame信息，通过jdbc的方式在doris中建表，则无需手动在doris中提前建表，

```
package com.hcdsj

import java.sql.DriverManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


object SparkDorisTest {


  def main(args: Array[String]): Unit = {
    val USER_DATABASES = "ds"
    val USER_TABLE = "test1"

    val sc = new SparkConf().setAppName("KafkaToHudi").setMaster("local[4]").set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val spark = SparkSession.builder().config(sc).getOrCreate()

    Class.forName("com.mysql.jdbc.Driver")
    val connection = DriverManager.getConnection("jdbc:mysql://172.31.41.20:9030/ds", "root", "")

    val frame = spark.readStream.format("hudi").load("/tmp/hudi/my_hudi_table")

    /**
     * 1.提取数据
     */
    val (fieldName,fieldType,pl) = frame.schema.fields.foldLeft("", "", "")(
      (z, f) => {
        if (z._1.nonEmpty && z._2.nonEmpty && z._3.nonEmpty) {
          //非空即表示不是第一次的时候进行拼接
          (z._1 + "," + f.name, z._2 + "," + f.name + " " + f.dataType.simpleString, z._3 + ",?")
        } else {
          (f.name, f.name + " " + f.dataType.simpleString, "?")
        }
      }
    )

    /**
     * 2.将spark的表达式转换为doris的表达式
     */
    val chCol: String = dfTypeName2CH(fieldType)

    /**
     * 3.建库
     */
    val createDatabaseSql =
      s"create database if not exists ${USER_DATABASES}";

    /**
     * 4.在doris中建表
     */
    val chTableSql =
      s"""
         |create table if not exists ${USER_DATABASES}.${USER_TABLE}(${chCol})
         |ENGINE=olap
         |DISTRIBUTED BY HASH(id) BUCKETS 10
         |PROPERTIES("replication_num" = "1")
         |""".stripMargin

    val i = connection.createStatement().executeUpdate(createDatabaseSql)
    val y = connection.createStatement().executeUpdate(chTableSql)
    if(y != -1){
      frame.writeStream.format("doris").option("doris.table.identifier", "${USER_DATABASES}.${USER_TABLE}").option("doris.fenodes", "172.31.41.20:8030").option("user", "root").option("password","").option("checkpointLocation", "/tmp/zhongtai/sq/t1_check").option("mode","Append").start().awaitTermination()

    }
    spark.stop()

  }

  /**
   * df
   * uid string gender string,...
   * ch
   * uid String gender String,...
   */
  def dfTypeName2CH(dfCol:String) = {
    dfCol.split(",").map(line => {
      val fields: Array[String] = line.split(" ")
      val fType: String = dfType2CHType(fields(1))
      val fName: String = fields(0)
      fName + " " + fType
    }).mkString(",")
  }

  /**
   * 将df的type转为ch的type
   */
  def dfType2CHType(fieldType: String):String = {
    fieldType.toLowerCase() match {
      case "string" => "VARCHAR(255)"
      case "integer" => "INT"
      case "long" => "BIGINT"
      case "float" => "FLOAT"
      case "double" => "DOUBLE"
      case "date" => "VARCHAR(255)"
      case "timestamp" => "VARCHAR(255)"
      case _ => "VARCHAR(255)"
    }
  }

}
```



#### 1





