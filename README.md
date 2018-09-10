# 微信小程序服务端

#### SSL证书
```
# CN=zlikun, OU=LeCheBang, O=LeCheBang.com, L=SH, ST=SH, C=CN
$ keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650

# 将生成的`keystore.p12`复制到工程根目录下，在`application.properties`中配置SSL即可
```