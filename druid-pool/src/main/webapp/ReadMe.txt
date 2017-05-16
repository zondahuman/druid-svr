create table team(
id int auto_increment primary key ,
team_name varchar(100),
create_time datetime,
update_time datetime,
version int
)


http://127.0.0.1:7100/druid/index.html
http://127.0.0.1:8300/druid/index.html


mysql -uroot -proot -h172.16.2.145 -P9066
mysql -uroot -proot -h172.16.2.145 -P8066


 Druid基本使用配置以及如何查看sql信息监控日志
http://blog.csdn.net/shukebai/article/details/51178658
