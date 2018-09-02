package com.zlikun.guava;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * Guava库的Hash工具类测试
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/9/2 15:40
 */
public class HashingTest {

    @Test
    public void hashing() throws UnsupportedEncodingException {
        String rawText = "十步杀一人，千里不留行";

        // 测试MD5
        HashFunction hf = Hashing.md5();
        assertEquals("106d6a60386b448f80d401758c203f54", hf.hashString(rawText, Charset.forName("ISO-8859-1")).toString());
        assertEquals("a116ad40bd5ab4c7518c355b978dbecd", hf.hashString(rawText, Charset.forName("UTF-8")).toString());
        assertEquals("a116ad40bd5ab4c7518c355b978dbecd", hf.hashString(rawText, Charset.defaultCharset()).toString());

        // 使用Spring提供的MD5工具类对比
        assertEquals("106d6a60386b448f80d401758c203f54", DigestUtils.md5DigestAsHex(rawText.getBytes("ISO-8859-1")));
        assertEquals("a116ad40bd5ab4c7518c355b978dbecd", DigestUtils.md5DigestAsHex(rawText.getBytes()));
        assertEquals("a116ad40bd5ab4c7518c355b978dbecd", DigestUtils.md5DigestAsHex(rawText.getBytes("UTF-8")));

//        // 似乎是代替MD5和SHA-1的算法，实际没太懂这玩意是干嘛的，每次生成 的摘要信息都会变化
//        hf = Hashing.goodFastHash(128);
//        assertEquals("931f057e122388d7a6f43af2da153b51", hf.hashString(rawText, Charset.forName("UTF-8")).toString());
//        assertEquals("d4fc8eb65bd516f7510100db04495fa4", hf.hashString(rawText, Charset.forName("ISO-8859-1")).toString());

        hf = Hashing.sha1();
        assertEquals("b9b3b118b6dce1e914e49797a6e1ccb58e26f99a", hf.hashString(rawText, Charset.forName("ISO-8859-1")).toString());
        assertEquals("c31c6240454f10effc51fd1328537bb01f1d7033", hf.hashString(rawText, Charset.forName("UTF-8")).toString());
        assertEquals("c31c6240454f10effc51fd1328537bb01f1d7033", hf.hashString(rawText, Charset.defaultCharset()).toString());

        hf = Hashing.sha256();
        assertEquals("b43878df519ded204282979315e505166699221526234b3d4986c22db9c1445b", hf.hashString(rawText, Charset.forName("ISO-8859-1")).toString());
        assertEquals("6477c31c28aa13a4e0125a271a8a0f1b7d5dc3a867925709f39f6b5cb7e3603a", hf.hashString(rawText, Charset.forName("UTF-8")).toString());
        assertEquals("6477c31c28aa13a4e0125a271a8a0f1b7d5dc3a867925709f39f6b5cb7e3603a", hf.hashString(rawText, Charset.defaultCharset()).toString());


    }

}
