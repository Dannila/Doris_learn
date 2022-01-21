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

### 4）、离线写入数据到Doris（flink）

##### 1、通过flink读取mysql数据写入doris

**使用Flink Doris Connector编译的doris-flink-1.0.0-flink-1.13.1_2.12.jar**

1.1、将编译的doris-flink-1.0.0-flink-1.13.1_2.12.jar复制到 `flink 的 `ClassPath` 中。

1.2、在doris里创建表

1.3、代码

```
package com.hcdsj

import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.TableEnvironment

object DorisSinkMysql {
  def main(args: Array[String]): Unit = {
    val settings = EnvironmentSettings.newInstance().build()
    val tenv = TableEnvironment.create(settings)
    val sql1 =
      """
        |CREATE TABLE tb1  (
        |  id INT NOT NULL,
        |  rule_name STRING,
        |  free_time_begin timestamp(3),
        |  free_time_end timestamp(3),
        |  vehicle_types STRING,
        |  processing_mode STRING,
        |  rule_status INT,
        |  create_time timestamp(3),
        |  update_time timestamp(3),
        |  timee timestamp(3),
        |  primary key(id) not enforced
        |) WITH (
        | 'connector' = 'mysql-cdc',
        | 'hostname' = '172.16.2.120',
        | 'port' = '3306',
        | 'username' = 'root',
        | 'password' = '123456',
        | 'database-name' = 'db_issue_clear',
        | 'table-name' = 'tb_nclear_dispute_setting'
        |)
        |""".stripMargin
      tenv.executeSql(sql1)

    val sql2 =
      """
        |CREATE TABLE flinks (
        |  id BIGINT ,
        |  rule_name STRING,
        |  free_time_begin timestamp(3),
        |  free_time_end timestamp(3),
        |  vehicle_types STRING,
        |  processing_mode STRING,
        |  rule_status INT,
        |  create_time timestamp(3),
        |  update_time timestamp(3),
        |  timee timestamp(3)
        |) WITH (
        | 'connector' = 'doris',
        | 'fenodes' = '172.16.2.123:8030',
        | 'table.identifier' = 'ds.mqflink1',
        | 'sink.batch.size' = '2',
        | 'sink.batch.interval'='1',
        | 'username' = 'root',
        | 'password'=''
        |)
        |""".stripMargin
    tenv.executeSql(sql2)

    tenv.executeSql("INSERT INTO flinks SELECT id,rule_name,free_time_begin,free_time_end,vehicle_types,processing_mode,rule_status,create_time,update_time,timee FROM tb1")

  }
}
```

1.4、打包

1.5、启动flink运行jar 写入数据

```
 ./bin/flink run -p 2 -c com.hcdsj.DorisSinkMysql -d /home/cxy/FlinkMysqlDoris-1.0-SNAPSHOT.jar
```

1.6、遇到的问题

```
org.apache.flink.client.program.ProgramInvocationException: The main method caused an error: Unable to instantiate java compiler
        at org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:372)
        at org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:222)
        at org.apache.flink.client.ClientUtils.executeProgram(ClientUtils.java:114)
        at org.apache.flink.client.cli.CliFrontend.executeProgram(CliFrontend.java:812)
        at org.apache.flink.client.cli.CliFrontend.run(CliFrontend.java:246)
        at org.apache.flink.client.cli.CliFrontend.parseAndRun(CliFrontend.java:1054)
        at org.apache.flink.client.cli.CliFrontend.lambda$main$10(CliFrontend.java:1132)
        at java.security.AccessController.doPrivileged(Native Method)
        at javax.security.auth.Subject.doAs(Subject.java:422)
        at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1893)
        at org.apache.flink.runtime.security.contexts.HadoopSecurityContext.runSecured(HadoopSecurityContext.java:41)
        at org.apache.flink.client.cli.CliFrontend.main(CliFrontend.java:1132)
Caused by: java.lang.IllegalStateException: Unable to instantiate java compiler
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.compile(JaninoRelMetadataProvider.java:428)
        at org.apache.calcite.rel.metadata.JaninoRelMetadataProvider.load3(JaninoRelMetadataProvider.java:374)

```

解决方法：

修改$FLINK_HOME/conf/flink-conf.yaml中

classloader.resolve-order: parent-first

参考链接：https://blog.csdn.net/appleyuchi/article/details/111597050

### 5）、实时写入数据到Doris（flink）

**使用Flink Doris Connector编译的doris-flink-1.0.0-flink-1.13.1_2.12.jar**

1.1、代码

```
package com.hcdsj

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object FlinkMysqlD {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val tenv = StreamTableEnvironment.create(env);
    val sql1 =
      """
        |CREATE TABLE tb1  (
        |  id INT NOT NULL,
        |  rule_name STRING,
        |  free_time_begin timestamp(3),
        |  free_time_end timestamp(3),
        |  vehicle_types STRING,
        |  processing_mode STRING,
        |  rule_status INT,
        |  create_time timestamp(3),
        |  update_time timestamp(3),
        |  timee timestamp(3),
        |  primary key(id) not enforced
        |) WITH (
        | 'connector' = 'mysql-cdc',
        | 'hostname' = '172.16.2.120',
        | 'port' = '3306',
        | 'username' = 'root',
        | 'password' = '123456',
        | 'database-name' = 'db_issue_clear',
        | 'table-name' = 'tb_nclear_dispute_setting'
        |)
        |""".stripMargin
    tenv.executeSql(sql1)

    val sql2 =
      """
        |CREATE TABLE flinks (
        |  id BIGINT ,
        |  rule_name STRING,
        |  free_time_begin timestamp(3),
        |  free_time_end timestamp(3),
        |  vehicle_types STRING,
        |  processing_mode STRING,
        |  rule_status INT,
        |  create_time timestamp(3),
        |  update_time timestamp(3),
        |  timee timestamp(3)
        |) WITH (
        | 'connector' = 'doris',
        | 'fenodes' = '172.16.2.123:8030',
        | 'table.identifier' = 'ds.mqflink1',
        | 'sink.batch.size' = '2',
        | 'sink.batch.interval'='1',
        | 'username' = 'root',
        | 'password'=''
        |)
        |""".stripMargin
    tenv.executeSql(sql2)
    tenv.executeSql("INSERT INTO flinks SELECT id,rule_name,free_time_begin,free_time_end,vehicle_types,processing_mode,rule_status,create_time,update_time,timee FROM tb1")
  }
}
```

启动flink 运行jar
```
./bin/flink run -c com.hcdsj.FlinkMysqlD -d /home/cxy/FlinkMysqlDoris-1.0-SNAPSHOT.jar
```

## 五、Doris数据采集方式与应用场景

为适配不同的数据导入需求，Doris 系统提供了不同的导入方式。每种导入方式支持不同的数据源，存在不同的使用方式（异步，同步）。

所有导入方式都支持 csv 数据格式。其中 Broker load 还支持 parquet 和 orc 数据格式。

每个导入方式的说明请参阅单个导入方式的操作手册。

官网地址：https://doris.apache.org/zh-CN/administrator-guide/load-data/load-manual.html#%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5

### 1)、Broker load

通过 Broker 进程访问并读取外部数据源（如 HDFS）导入到 Doris。用户通过 Mysql 协议提交导入作业后，异步执行。通过 `SHOW LOAD` 命令查看导入结果。

官网地址：https://doris.apache.org/branch-0.15/zh-CN/administrator-guide/load-data/broker-load-manual.html#%E9%80%82%E7%94%A8%E5%9C%BA%E6%99%AF

#### 1、适用场景

1.1、源数据在 Broker 可以访问的存储系统中，如 HDFS。

1.2、数据量在 几十到百GB 级别。

#### 2、基本原理

用户在提交导入任务后，FE 会生成对应的 Plan 并根据目前 BE 的个数和文件的大小，将 Plan 分给 多个 BE 执行，每个 BE 执行一部分导入数据。

BE 在执行的过程中会从 Broker 拉取数据，在对数据 transform 之后将数据导入系统。所有 BE 均完成导入，由 FE 最终决定导入是否成功。

**名词解释：**

1. Frontend（FE）：Doris 系统的元数据和调度节点。在导入流程中主要负责导入 plan 生成和导入任务的调度工作。
2. Backend（BE）：Doris 系统的计算和存储节点。在导入流程中主要负责数据的 ETL 和存储。
3. Broker：Broker 为一个独立的无状态进程。封装了文件系统接口，提供 Doris 读取远端存储系统中文件的能力。
4. Plan：导入执行计划，BE 会执行导入执行计划将数据导入到 Doris 系统中。

#### 3、基本操作

##### 3.1、创建导入

Broker load 创建导入语句

```
LOAD LABEL db_name.label_name 
(data_desc, ...)
WITH BROKER broker_name broker_properties
[PROPERTIES (key1=value1, ... )]

* data_desc:

    DATA INFILE ('file_path', ...)
    [NEGATIVE]
    INTO TABLE tbl_name
    [PARTITION (p1, p2)]
    [COLUMNS TERMINATED BY separator ]
    [(col1, ...)]
    [PRECEDING FILTER predicate]
    [SET (k1=f1(xx), k2=f2(xx))]
    [WHERE predicate]

* broker_properties: 

    (key1=value1, ...)
```

示例：

```
LOAD LABEL db1.label1
(
    DATA INFILE("hdfs://abc.com:8888/user/palo/test/ml/file1")
    INTO TABLE tbl1
    COLUMNS TERMINATED BY ","
    (tmp_c1,tmp_c2)
    SET
    (
        id=tmp_c2,
        name=tmp_c1
    ),
    DATA INFILE("hdfs://abc.com:8888/user/palo/test/ml/file2")
    INTO TABLE tbl2
    COLUMNS TERMINATED BY ","
    (col1, col2)
    where col1 > 1
)
WITH BROKER 'broker'
(
    "username"="user",
    "password"="pass"
)
PROPERTIES
(
    "timeout" = "3600"
);
```

Label 的另一个作用，是防止用户重复导入相同的数据。**强烈推荐用户同一批次数据使用相同的label。这样同一批次数据的重复请求只会被接受一次，保证了 At-Most-Once 语义**

##### 3.2、查看导入

Broker load 导入方式由于是异步的，所以用户必须将创建导入的 Label 记录，并且在**查看导入命令中使用 Label 来查看导入结果**。查看导入命令在所有导入方式中是通用的，具体语法可执行 `HELP SHOW LOAD` 查看。

```
mysql> show load order by createtime desc limit 1\G
*************************** 1. row ***************************
         JobId: 76391
         Label: label1
         State: FINISHED
      Progress: ETL:N/A; LOAD:100%
          Type: BROKER
       EtlInfo: unselected.rows=4; dpp.abnorm.ALL=15; dpp.norm.ALL=28133376
      TaskInfo: cluster:N/A; timeout(s):10800; max_filter_ratio:5.0E-5
      ErrorMsg: N/A
    CreateTime: 2019-07-27 11:46:42
  EtlStartTime: 2019-07-27 11:46:44
 EtlFinishTime: 2019-07-27 11:46:44
 LoadStartTime: 2019-07-27 11:46:44
LoadFinishTime: 2019-07-27 11:50:16
           URL: http://192.168.1.1:8040/api/_load_error_log?file=__shard_4/error_log_insert_stmt_4bb00753932c491a-a6da6e2725415317_4bb00753932c491a_a6da6e2725415317
    JobDetails: {"Unfinished backends":{"9c3441027ff948a0-8287923329a2b6a7":[10002]},"ScannedRows":2390016,"TaskNumber":1,"All backends":{"9c3441027ff948a0-8287923329a2b6a7":[10002]},"FileNumber":1,"FileSize":1073741824}
```

##### 3.3、取消导入

当 Broker load 作业状态不为 CANCELLED 或 FINISHED 时，可以被用户手动取消。取消时需要指定待取消导入任务的 Label 。取消导入命令语法可执行 `HELP CANCEL LOAD`查看。

#### 4、应用场景

4.1、原始数据在文件系统（HDFS，BOS，AFS）中是最适合使用 Broker load 的场景。

4.2、如果用户在导入大文件中，需要使用异步接入，也可以考虑使用 Broker load。因为 Broker load 是单次导入中唯一的一种异步导入的方式。

#### 5、应用案例

数据情况：用户数据在 HDFS 中，文件地址为 hdfs://abc.com:8888/store_sales, hdfs 的认证用户名为 root, 密码为 password, 数据量大小约为 30G，希望导入到数据库 bj_sales 的表 store_sales 中。

集群情况：集群的 BE 个数约为 3 个，Broker 名称均为 broker。

step1: 经过上述方法的计算，本次导入的单个 BE 导入量为 10G，则需要先修改 FE 的配置，将单个 BE 导入最大量修改为：

```
max_bytes_per_broker_scanner = 10737418240
```

step2: 经计算，本次导入的时间大约为 1000s，并未超过默认超时时间，可不配置导入自定义超时时间。

step3：创建导入语句

```
LOAD LABEL bj_sales.store_sales_broker_load_01
(
    DATA INFILE("hdfs://abc.com:8888/store_sales")
    INTO TABLE store_sales
)
WITH BROKER 'broker'
("username"="root", "password"="password");
```

### 2)、Stream load

用户通过 HTTP 协议提交请求并携带原始数据创建导入。主要用于快速将本地文件或数据流中的数据导入到 Doris。导入命令同步返回导入结果。

官网地址：https://doris.apache.org/branch-0.15/zh-CN/administrator-guide/load-data/stream-load-manual.html#fe-%E9%85%8D%E7%BD%AE

#### 1、适用场景

Stream load 主要适用于导入本地文件，或通过程序导入数据流中的数据。

**支持数据格式**

目前 Stream Load 支持两个数据格式：CSV（文本） 和 JSON

#### 2、基本原理

Stream load 中，Doris 会选定一个节点作为 Coordinator 节点。该节点负责接数据并分发数据到其他数据节点。

用户通过 HTTP 协议提交导入命令。如果提交到 FE，则 FE 会通过 HTTP redirect 指令将请求转发给某一个 BE。用户也可以直接提交导入命令给某一指定 BE。

导入的最终结果由 Coordinator BE 返回给用户

#### 3、基本操作

##### 3.1、创建导入

Stream load 通过 HTTP 协议提交和传输数据。这里通过 `curl` 命令展示如何提交导入。

用户也可以通过其他 HTTP client 进行操作。

```
curl --location-trusted -u user:passwd [-H ""...] -T data.file -XPUT http://fe_host:http_port/api/{db}/{table}/_stream_load

Header 中支持属性见下面的 ‘导入任务参数’ 说明 
格式为: -H "key1:value1"
```

示例

```
curl --location-trusted -u root -T date -H "label:123" http://abc.com:8030/api/test/date/_stream_load
```

##### 3.2、返回结果

由于 Stream load 是一种同步的导入方式，所以导入的结果会通过创建导入的返回值直接返回给用户。

示例：

```
{
    "TxnId": 1003,
    "Label": "b6f3bc78-0d2c-45d9-9e4c-faa0a0149bee",
    "Status": "Success",
    "ExistingJobStatus": "FINISHED", // optional
    "Message": "OK",
    "NumberTotalRows": 1000000,
    "NumberLoadedRows": 1000000,
    "NumberFilteredRows": 1,
    "NumberUnselectedRows": 0,
    "LoadBytes": 40888898,
    "LoadTimeMs": 2144,
    "BeginTxnTimeMs": 1,
    "StreamLoadPutTimeMs": 2,
    "ReadDataTimeMs": 325,
    "WriteDataTimeMs": 1933,
    "CommitAndPublishTimeMs": 106,
    "ErrorURL": "http://192.168.1.1:8042/api/_load_error_log?file=__shard_0/error_log_insert_stmt_db18266d4d9b4ee5-abb00ddd64bdf005_db18266d4d9b4ee5_abb00ddd64bdf005"
}
```

##### 3.3、取消导入

用户无法手动取消 Stream load，Stream load 在超时或者导入错误后会被系统自动取消

#### 4、应用场景

使用 Stream load 的最合适场景就是原始文件在内存中，或者在磁盘中。其次，由于 Stream load 是一种同步的导入方式，所以用户如果希望用同步方式获取导入结果，也可以使用这种导入。

#### 5、应用案例

数据情况： 数据在发送导入请求端的本地磁盘路径 /home/store_sales 中，导入的数据量约为 15G，希望导入到数据库 bj_sales 的表 store_sales 中。

集群情况：Stream load 的并发数不受集群大小影响。

step1: 导入文件大小是否超过默认的最大导入大小10G

```
修改 BE conf
streaming_load_max_mb = 16000
```

step2: 计算大概的导入时间是否超过默认 timeout 值

```
导入时间 ≈ 15000 / 10 = 1500s
超过了默认的 timeout 时间，需要修改 FE 的配置
stream_load_default_timeout_second = 1500
```

step3：创建导入任务

```
curl --location-trusted -u user:password -T /home/store_sales -H "label:abc" http://abc.com:8000/api/bj_sales/store_sales/_stream_load
```

### 3)、Insert

Insert Into 语句的使用方式和 MySQL 等数据库中 Insert Into 语句的使用方式类似。但在 Doris 中，所有的数据写入都是一个独立的导入作业。所以这里将 Insert Into 也作为一种导入方式介绍。

主要的 Insert Into 命令包含以下两种；

- INSERT INTO tbl SELECT ...
- INSERT INTO tbl (col1, col2, ...) VALUES (1, 2, ...), (1,3, ...);

其中第二种命令仅用于 Demo，不要使用在测试或生产环境中。

官网地址：https://doris.apache.org/branch-0.15/zh-CN/administrator-guide/load-data/insert-into-manual.html

#### 1、基本操作

##### 1.1、创建导入

Insert Into 命令需要通过 MySQL 协议提交，创建导入请求会同步返回导入结果。

语法：

```
INSERT INTO table_name [partition_info] [WITH LABEL label] [col_list] [query_stmt] [VALUES];
```

示例

```
INSERT INTO tbl2 WITH LABEL label1 SELECT * FROM tbl3;
INSERT INTO tbl1 VALUES ("qweasdzxcqweasdzxc"), ("a");
```

注1：当需要使用 `CTE(Common Table Expressions)` 作为 insert 操作中的查询部分时，必须指定 `WITH LABEL` 和 column list 部分。

示例

```
INSERT INTO tbl1 WITH LABEL label1
WITH cte1 AS (SELECT * FROM tbl1), cte2 AS (SELECT * FROM tbl2)
SELECT k1 FROM cte1 JOIN cte2 WHERE cte1.k1 = 1;


INSERT INTO tbl1 (k1)
WITH cte1 AS (SELECT * FROM tbl1), cte2 AS (SELECT * FROM tbl2)
SELECT k1 FROM cte1 JOIN cte2 WHERE cte1.k1 = 1;
```

##### 1.2、导入结果

Insert Into 本身就是一个 SQL 命令，其返回结果会根据执行结果的不同，分为以下几种：

1.2.1、结果集为空

如果 insert 对应 select 语句的结果集为空，则返回如下：

```
mysql> insert into tbl1 select * from empty_tbl;
Query OK, 0 rows affected (0.02 sec)
```

`Query OK` 表示执行成功。`0 rows affected` 表示没有数据被导入。

1.2.2、结果集不为空

在结果集不为空的情况下。返回结果分为如下几种情况：

- Insert 执行成功并可见：

  ```
  mysql> insert into tbl1 select * from tbl2;
  Query OK, 4 rows affected (0.38 sec)
  {'label':'insert_8510c568-9eda-4173-9e36-6adc7d35291c', 'status':'visible', 'txnId':'4005'}
  
  mysql> insert into tbl1 with label my_label1 select * from tbl2;
  Query OK, 4 rows affected (0.38 sec)
  {'label':'my_label1', 'status':'visible', 'txnId':'4005'}
  
  mysql> insert into tbl1 select * from tbl2;
  Query OK, 2 rows affected, 2 warnings (0.31 sec)
  {'label':'insert_f0747f0e-7a35-46e2-affa-13a235f4020d', 'status':'visible', 'txnId':'4005'}
  
  mysql> insert into tbl1 select * from tbl2;
  Query OK, 2 rows affected, 2 warnings (0.31 sec)
  {'label':'insert_f0747f0e-7a35-46e2-affa-13a235f4020d', 'status':'committed', 'txnId':'4005'}
  ```

  `Query OK` 表示执行成功。`4 rows affected` 表示总共有4行数据被导入。`2 warnings` 表示被过滤的行数。

  同时会返回一个 json 串：

  ```
  {'label':'my_label1', 'status':'visible', 'txnId':'4005'}
  {'label':'insert_f0747f0e-7a35-46e2-affa-13a235f4020d', 'status':'committed', 'txnId':'4005'}
  {'label':'my_label1', 'status':'visible', 'txnId':'4005', 'err':'some other error'}
  ```

  可以通过如下语句查看这批数据的可见状态：

  ```
  show transaction where id=4005;
  ```

- Insert 执行失败

  执行失败表示没有任何数据被成功导入，并返回如下：

  ```
  mysql> insert into tbl1 select * from tbl2 where k1 = "a";
  ERROR 1064 (HY000): all partitions have no load data. url: http://10.74.167.16:8042/api/_load_error_log?file=__shard_2/error_log_insert_stmt_ba8bb9e158e4879-ae8de8507c0bf8a2_ba8bb9e158e4879_ae8de8507c0bf8a2
  ```

#### 2、应用场景

1. 用户希望仅导入几条假数据，验证一下 Doris 系统的功能。此时适合使用 INSERT INTO VALUES 的语法。
2. 用户希望将已经在 Doris 表中的数据进行 ETL 转换并导入到一个新的 Doris 表中，此时适合使用 INSERT INTO SELECT 语法。
3. 用户可以创建一种外部表，如 MySQL 外部表映射一张 MySQL 系统中的表。或者创建 Broker 外部表来映射 HDFS 上的数据文件。然后通过 INSERT INTO SELECT 语法将外部表中的数据导入到 Doris 表中存储。

#### 3、应用案例

用户有一张表 store_sales 在数据库 sales 中，用户又创建了一张表叫 bj_store_sales 也在数据库 sales 中，用户希望将 store_sales 中销售记录在 bj 的数据导入到这张新建的表 bj_store_sales 中。导入的数据量约为：10G。

```
store_sales schema：
(id, total, user_id, sale_timestamp, region)

bj_store_sales schema:
(id, total, user_id, sale_timestamp)
```

集群情况：用户当前集群的平均导入速度约为 5M/s

Step1: 判断是否要修改 Insert Into 的默认超时时间

```
计算导入的大概时间
10G / 5M/s = 2000s

修改 FE 配置
insert_load_default_timeout_second = 2000
```

Step2：创建导入任务

由于用户是希望将一张表中的数据做 ETL 并导入到目标表中，所以应该使用 Insert into query_stmt 方式导入。

```
INSERT INTO bj_store_sales WITH LABEL `label` SELECT id, total, user_id, sale_timestamp FROM store_sales where region = "bj";
```

### 4)、Multi load

用户通过 HTTP 协议提交多个导入作业。Multi Load 可以保证多个导入作业的原子生效。

### 5）、Routine load

用户通过 MySQL 协议提交例行导入作业，生成一个常驻线程，不间断的从数据源（如 Kafka）中读取数据并导入到 Doris 中。

官网地址：https://doris.apache.org/branch-0.15/zh-CN/administrator-guide/load-data/routine-load-manual.html

#### 1、基本原理

Client 向 FE 提交一个例行导入作业。

FE 通过 JobScheduler 将一个导入作业拆分成若干个 Task。每个 Task 负责导入指定的一部分数据。Task 被 TaskScheduler 分配到指定的 BE 上执行。

在 BE 上，一个 Task 被视为一个普通的导入任务，通过 Stream Load 的导入机制进行导入。导入完成后，向 FE 汇报。

FE 中的 JobScheduler 根据汇报结果，继续生成后续新的 Task，或者对失败的 Task 进行重试。

整个例行导入作业通过不断的产生新的 Task，来完成数据不间断的导入。

**名词解释**

- FE：Frontend，Doris 的前端节点。负责元数据管理和请求接入。
- BE：Backend，Doris 的后端节点。负责查询执行和数据存储。
- RoutineLoadJob：用户提交的一个例行导入作业。
- JobScheduler：例行导入作业调度器，用于调度和拆分一个 RoutineLoadJob 为多个 Task。
- Task：RoutineLoadJob 被 JobScheduler 根据规则拆分的子任务。
- TaskScheduler：任务调度器。用于调度 Task 的执行。

#### 2、Kafka 例行导入

当前我们仅支持从 Kafka 系统进行例行导入。该部分会详细介绍 Kafka 例行导入使用方式和最佳实践。

##### 2.1、使用限制

1. 支持无认证的 Kafka 访问，以及通过 SSL 方式认证的 Kafka 集群。
2. 支持的消息格式为 csv, json 文本格式。csv 每一个 message 为一行，且行尾**不包含**换行符。
3. 仅支持 Kafka 0.10.0.0(含) 以上版本。

##### 2.2、创建例行导入任务

创建例行导入任务的的详细语法可以连接到 Doris 后，执行 `HELP ROUTINE LOAD;`查看语法帮助。

```
CREATE ROUTINE LOAD db1.job1 on tbl1
PROPERTIES
(
    "desired_concurrent_number"="1"
)
FROM KAFKA
(
    "kafka_broker_list"= "broker1:9091,broker2:9091",
    "kafka_topic" = "my_topic",
    "property.security.protocol" = "ssl",
    "property.ssl.ca.location" = "FILE:ca.pem",
    "property.ssl.certificate.location" = "FILE:client.pem",
    "property.ssl.key.location" = "FILE:client.key",
    "property.ssl.key.password" = "abcdefg"
);
```

##### 2.3、查看导入作业状态

查看**作业**状态的具体命令和示例可以通过 `HELP SHOW ROUTINE LOAD;` 命令查看。

查看**任务**运行状态的具体命令和示例可以通过 `HELP SHOW ROUTINE LOAD TASK;` 命令查看。

只能查看当前正在运行中的任务，已结束和未开始的任务无法查看。

##### 2.4、修改作业属性

用户可以修改已经创建的作业。具体说明可以通过 `HELP ALTER ROUTINE LOAD;` 命令查看。或参阅 https://doris.apache.org/branch-0.15/zh-CN/sql-reference/sql-statements/Data%20Manipulation/alter-routine-load.html#description。

##### 2.5、作业控制

用户可以通过 `STOP/PAUSE/RESUME` 三个命令来控制作业的停止，暂停和重启。可以通过 `HELP STOP ROUTINE LOAD;`, `HELP PAUSE ROUTINE LOAD;` 以及 `HELP RESUME ROUTINE LOAD;` 三个命令查看帮助和示例。

### 6）、通过S3协议直接导入

从0.14 版本开始，Doris 支持通过S3协议直接从支持S3协议的在线存储系统导入数据。

本文档主要介绍如何导入 AWS S3 中存储的数据。也支持导入其他支持S3协议的对象存储系统导入，如果百度云的BOS，阿里云的OSS和腾讯云的COS等

#### 1、适用场景

- 源数据在 支持S3协议的存储系统中，如 S3,BOS 等。
- 数据量在 几十到百GB 级别。

#### 2、基本操作

导入方式和Broker Load 基本相同，只需要将 `WITH BROKER broker_name ()` 语句替换成如下部分

```
WITH S3
    (
        "AWS_ENDPOINT" = "AWS_ENDPOINT",
        "AWS_ACCESS_KEY" = "AWS_ACCESS_KEY",
        "AWS_SECRET_KEY"="AWS_SECRET_KEY",
        "AWS_REGION" = "AWS_REGION"
    )
```

完整示例如下

```
 LOAD LABEL example_db.exmpale_label_1
    (
        DATA INFILE("s3://your_bucket_name/your_file.txt")
        INTO TABLE load_test
        COLUMNS TERMINATED BY ","
    )
    WITH S3
    (
        "AWS_ENDPOINT" = "AWS_ENDPOINT",
        "AWS_ACCESS_KEY" = "AWS_ACCESS_KEY",
        "AWS_SECRET_KEY"="AWS_SECRET_KEY",
        "AWS_REGION" = "AWS_REGION"
    )
    PROPERTIES
    (
        "timeout" = "3600"
    );
```

### 7）、Binlog Load

Binlog Load提供了一种使Doris增量同步用户在Mysql数据库的对数据更新操作的CDC(Change Data Capture)功能。

官网地址：https://doris.apache.org/branch-0.15/zh-CN/administrator-guide/load-data/binlog-load-manual.html

#### 1、适用场景

- INSERT/UPDATE/DELETE支持
- 过滤Query
- 暂不兼容DDL语句

#### 2、基本原理

用户向FE提交一个数据同步作业。

FE会为每个数据同步作业启动一个canal client，来向canal server端订阅并获取数据。

client中的receiver将负责通过Get命令接收数据，每获取到一个数据batch，都会由consumer根据对应表分发到不同的channel，每个channel都会为此数据batch产生一个发送数据的子任务Task。

在FE上，一个Task是channel向BE发送数据的子任务，里面包含分发到当前channel的同一个batch的数据。

channel控制着单个表事务的开始、提交、终止。一个事务周期内，一般会从consumer获取到多个batch的数据，因此会产生多个向BE发送数据的子任务Task，在提交事务成功前，这些Task不会实际生效。

满足一定条件时（比如超过一定时间、达到提交最大数据大小），consumer将会阻塞并通知各个channel提交事务。

当且仅当所有channel都提交成功，才会通过Ack命令通知canal并继续获取并消费数据。

如果有任意channel提交失败，将会重新从上一次消费成功的位置获取数据并再次提交（已提交成功的channel不会再次提交以保证幂等性）。

整个数据同步作业中，FE通过以上流程不断的从canal获取数据并提交到BE，来完成数据同步。

#### 3、基本操作

##### 3.1、配置目标表属性

用户需要先在Doris端创建好与Mysql端对应的目标表

Binlog Load只能支持Unique类型的目标表，且必须激活目标表的Batch Delete功能。

开启Batch Delete的方法可以参考`help alter table`中的批量删除功能。

示例：

```
-- create target table
CREATE TABLE `test1` (
  `a` int(11) NOT NULL COMMENT "",
  `b` int(11) NOT NULL COMMENT ""
) ENGINE=OLAP
UNIQUE KEY(`a`)
COMMENT "OLAP"
DISTRIBUTED BY HASH(`a`) BUCKETS 8;

-- enable batch delete
ALTER TABLE canal_test.test1 ENABLE FEATURE "BATCH_DELETE";
```

##### 3.2、创建同步作业

创建数据同步作业的的详细语法可以连接到 Doris 后，执行 HELP CREATE SYNC JOB; 查看语法帮助。这里主要详细介绍，创建作业时的注意事项。

- job_name

  `job_name`是数据同步作业在当前数据库内的唯一标识，相同`job_name`的作业只能有一个在运行。

- channel_desc

  `channel_desc`用来定义任务下的数据通道，可表示mysql源表到doris目标表的映射关系。在设置此项时，如果存在多个映射关系，必须满足mysql源表应该与doris目标表是一一对应关系，其他的任何映射关系（如一对多关系），检查语法时都被视为不合法。

- column_mapping

  `column_mapping`主要指mysql源表和doris目标表的列之间的映射关系，如果不指定，FE会默认源表和目标表的列按顺序一一对应。但是我们依然建议显式的指定列的映射关系，这样当目标表的结构发生变化（比如增加一个 nullable 的列），数据同步作业依然可以进行。否则，当发生上述变动后，因为列映射关系不再一一对应，导入将报错。

- binlog_desc

  `binlog_desc`中的属性定义了对接远端Binlog地址的一些必要信息，目前可支持的对接类型只有canal方式，所有的配置项前都需要加上canal前缀。

  1. `canal.server.ip`: canal server的地址
  2. `canal.server.port`: canal server的端口
  3. `canal.destination`: 前文提到的instance的字符串标识
  4. `canal.batchSize`: 每批从canal server处获取的batch大小的最大值，默认8192
  5. `canal.username`: instance的用户名
  6. `canal.password`: instance的密码
  7. `canal.debug`: 设置为true时，会将batch和每一行数据的详细信息都打印出来，会影响性能。

##### 3.3、查看作业状态

查看作业状态的具体命令和示例可以通过 `HELP SHOW SYNC JOB;` 命令查看。

返回结果集的参数意义如下：

- State

  作业当前所处的阶段。作业状态之间的转换如下图所示：

  ```
     create job  |  PENDING    |    resume job
         +-----------+             <-------------+
         |           +-------------+             |
    +----v-------+                       +-------+----+
    |  RUNNING   |     pause job         |  PAUSED    |
    |            +----------------------->            |
    +----+-------+     run error         +-------+----+
         |           +-------------+             |
         |           | CANCELLED   |             |
         +----------->             <-------------+
        stop job     +-------------+    stop job
        system error
  ```

  作业提交之后状态为PENDING，由FE调度执行启动canal client后状态变成RUNNING，用户可以通过 STOP/PAUSE/RESUME 三个命令来控制作业的停止，暂停和恢复，操作后作业状态分别为CANCELLED/PAUSED/RUNNING。

  作业的最终阶段只有一个CANCELLED，当作业状态变为CANCELLED后，将无法再次恢复。当作业发生了错误时，若错误是不可恢复的，状态会变成CANCELLED，否则会变成PAUSED。

##### 3.4、控制作业

用户可以通过 STOP/PAUSE/RESUME 三个命令来控制作业的停止，暂停和恢复。可以通过`HELP STOP SYNC JOB`; `HELP PAUSE SYNC JOB`; 以及 `HELP RESUME SYNC JOB`; 三个命令查看帮助和示例。



